(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close!]])
    (:import [chaperone.crossover.rpc Request]))

;; system

(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers         (all-edn-readers)
                      :request-chan        (chan)
                      :request-chan-listen (atom false)
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
        (reset! (:request-chan-listen rpc) true)
        (go (while (-> rpc :request-chan-listen deref)
                (let [request (<! (:request-chan rpc))
                      data (run-rpc-request system request)]
                    (>! (:response-chan rpc) (new-response request data))))))
    system)

(defn stop!
    "Stop the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (when rpc
            (reset! (:request-chan-listen rpc) false)
            (close! (:request-chan rpc))
            (close! (:response-chan rpc))))
    system)