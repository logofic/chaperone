(ns ^{:doc "Crossover: System user and user management"}
    chaperone.crossover.user
    (:require [cljs-uuid.core :as uuid]))

;;; User record
(defrecord User [id firstname lastname password email photo last-logged-in])

(defn new-user
    "Constructor function for a new user. Also sets the ID to a UUID apon creation."
    [firstname lastname email password
     & {:keys [photo last-logged-in]}]
    (->User (uuid/make-random-string) firstname lastname password email photo last-logged-in))

(defn edn-readers
    "EDN readers map for this namespace"
    []
    {'chaperone.crossover.user.User map->User})