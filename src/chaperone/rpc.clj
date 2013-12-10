(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close!]])
    )

;; system
(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers          (edn-readers)
                      :request-chan         (chan)
                      :request-chan-listen  (atom false)
                      :response-chan        (chan)
                      :response-chan-listen (atom false)}]
        (assoc system :rpc sub-system)))

(defn sub-system
    "RPC sub system"
    [system]
    (:rpc system))

(defn start!
    "Start the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (reset! (:request-chan-listen rpc) true)
        (reset! (:response-chan-listen rpc) true))
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