(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan]])
    )

;; system
(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers   (edn-readers)
                      :request-chan  (chan)
                      :request-chan-listen (atom false)
                      :response-chan (chan)
                      :response-chan-listen (atom false)}]
        (assoc system :srpc sub-system)))

(defn sub-system
    "RPC sub system"
    [system]
    (:rpc system))
