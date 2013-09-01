(ns
	^{:doc "System user and user management"}
	chaperone.user
	(:require [chaperone.persistence.core :as pcore]))

;;; mapping configuration
(defrecord User [id
								 firstname
								 lastname
								 password
								 email
								 photo
								 last-logged-in]
	pcore/Persistent
	(get-type [this]
		"Returns the es type of this persistence record"
		"user"))

(defn new-user
	"Constructor function for a new user. Also sets the ID to a UUID apon creation."
	[firstname lastname email password
	 & {:keys [photo last-logged-in]}]
	(->User (pcore/create-id) firstname lastname password email photo last-logged-in))

#_
(defn map->User [map]
	(->User (:id map) firstname lastname password email photo last-logged-in))