(ns ^{:doc "System user and user management"}
	chaperone.user
	(:require [chaperone.persistence.core :as pcore]
			  [clojurewerkz.elastisch.query :as esq]))

;;; User record
(defrecord User
	[id firstname lastname password email photo last-logged-in]
	pcore/Persistent
	(get-type [this]
		"Returns the es type of this persistence record"
		"user"))

(defn new-user
	"Constructor function for a new user. Also sets the ID to a UUID apon creation."
	[firstname lastname email password
	 & {:keys [photo last-logged-in]}]
	(->User (pcore/create-id) firstname lastname password email photo last-logged-in))

(defn _source->User
	"Create a User from the elasicsearch _source map"
	[map]
	(->User (:id map) (:firstname map) (:lastname map) (:password map) (:email map) (:photo map)
			(pcore/parse-string-date (:last-logged-in map))))

(defn list-users
	"list all users"
	[]
	(pcore/search-to-record "user" _source->User :query (esq/match-all) :sort {:lastname "asc"}))