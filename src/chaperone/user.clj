(ns
	^{:doc "System user and user management"}
	chaperone.user
	(:require [cljs-uuid.core :as uuid]))

(defrecord User [id
								 firstname
								 lastname
								 password
								 email
								 photo
								 last-logged-in])

(defn new-user
	"Constructor function for a new user. Also sets the ID to a UUID apon creation."
	[firstname lastname email password
	 & {:keys [photo last-logged-in]}]
	(->User (uuid/make-random) firstname lastname password email photo last-logged-in))