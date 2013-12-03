(ns ^{:doc "Crossover: RPC mechanism over web sockets"}
    chaperone.crossover.rpc
    (:require [cljs-uuid.core :as uuid]))

(defrecord Request [id category action data])
(defrecord Response [^Request request data])

(defn new-request
    "Constructor function: Create a new RPC request"
    [category action data]
    (->Request (uuid/make-random) category action data))

(defn new-response
    "Constructor function: Create a new RPC response"
    [^Request request data]
    (->Response request data))