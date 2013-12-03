(ns ^{:doc "Crossover: RPC mechanism over web sockets"}
    chaperone.crossover.rpc)

(defrecord Request [id category action data])
(defrecord Response [^Request request data])
