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

(defn make-user [property-map]
	"Creates a default user, with the id generated for it"
	(let [new-user-map (assoc property-map :id (uuid/make-random))]
		(map->User new-user-map)))