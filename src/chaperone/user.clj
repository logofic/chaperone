(ns
	^{:doc "System user and user management"}
	chaperone.user)

(defrecord User [id
								 firstname
								 lastname
								 password
								 email
								 photo
								 last-logged-in])