(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close!]]
          [while-let.core])
    (:import [chaperone.crossover.rpc Request]))

;; system

(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers         (all-edn-readers)
                      :request-chan        (chan)
                      :response-chan       (chan)}]
        (assoc system :rpc sub-system)))

(defn sub-system
    "RPC sub system"
    [system]
    (:rpc system))

(defmulti rpc-handler
          "Is the function for a given request of type [:category :action] and returns the data to be sent back in the response"
          (fn [system ^Request request] [(:category request) (:action request)]))

(defn run-rpc-request
    "Actually run the function for a request"
    [system ^Request request]
    ;; on stop, the request can be nil. Ignore that
    (when request
        (rpc-handler system request)))

(defn start!
    "Start the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (go (while-let [request (<! (:request-chan rpc))]
                (let [data (run-rpc-request system request)]
                    (>! (:response-chan rpc) (new-response request data))))))
    system)

(defn stop!
    "Stop the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (when rpc
            (close! (:request-chan rpc))
            (close! (:response-chan rpc))))
    system)