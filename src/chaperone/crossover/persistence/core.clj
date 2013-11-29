(ns ^{:doc "Core functionality for persistance, crossovered"}
    chaperone.crossover.persistence.core
    (:require [cljs-uuid.core :as uuid]))

(defn create-id
    "creates a uuid string"
    []
    (-> (uuid/make-random) .toString))
