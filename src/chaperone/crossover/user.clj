(ns ^{:doc "Crossover: System user and user management"}
    chaperone.crossover.user
    (:require [chaperone.crossover.persistence.core :as pcore]))

;;; User record
(defrecord User [id firstname lastname password email photo last-logged-in])

(defn new-user
    "Constructor function for a new user. Also sets the ID to a UUID apon creation."
    [firstname lastname email password
     & {:keys [photo last-logged-in]}]
    (->User (pcore/create-id) firstname lastname password email photo last-logged-in))