(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close!]]
          [alex-and-georges.debug-repl])
    (:import [chaperone.crossover.rpc Request])
    (:require [chaperone.user :as user]))

;; system

(defn- create-rpc-reponse-map
    [system]
    {:user (user/rpc-response-map system)})

(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers          (edn-readers)
                      :request-chan         (chan)
                      :request-chan-listen  (atom false)
                      :response-chan        (chan)
                      :response-chan-listen (atom false)
                      :rpc-handler-map      (create-rpc-reponse-map system)}]
        (assoc system :rpc sub-system)))

(defn sub-system
    "RPC sub system"
    [system]
    (:rpc system))

(defn run-rpc-request
    "Actually run the function for a request"
    [rpc ^Request request]
    ;; on stop, the request can be nil. Ignore that
    (when request
        (let [handlers (:rpc-handler-map rpc)
              f (get-in handlers [(:category request) (:action request)])]
            (f (:data request)))))

(defn start!
    "Start the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (reset! (:request-chan-listen rpc) true)
        (reset! (:response-chan-listen rpc) true)
        (go (while (-> rpc :request-chan-listen deref)
                (let [request (<! (:request-chan rpc))
                      data (run-rpc-request rpc request)]
                    (>! (:response-chan rpc) (new-response request data))))))
    system)

(defn stop!
    "Stop the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (when rpc
            (reset! (:request-chan-listen rpc) false)
            (reset! (:response-chan-listen rpc) false)
            (close! (:request-chan rpc))
            (close! (:response-chan rpc))))
    system)