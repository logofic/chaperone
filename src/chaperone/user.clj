(ns ^{:doc "System user and user management"}
    chaperone.user
    (:use [chaperone.crossover.user])
    (:import chaperone.crossover.user.User
             chaperone.crossover.rpc.Request)
    (:require [chaperone.persistence.core :as pcore]
              [clojurewerkz.elastisch.query :as esq]
              [clojurewerkz.scrypt.core :as sc]
              [chaperone.web.rpc :as rpc]))

(defmethod pcore/get-type User [record] "user")

(defn _source->User
    "Create a User from the elasicsearch _source map"
    [persistence map]
    (->User (:id map) (:firstname map) (:lastname map) (:password map) (:email map) (:photo map)
            (pcore/parse-string-date persistence (:last-logged-in map))))

(defn list-users
    "list all users"
    [persistence]
    (pcore/search-to-record persistence "user" (partial _source->User persistence) :query (esq/match-all) :sort {:lastname "asc"}))

(defn get-user-by-id
    "get a specific user by an id"
    [persistence id]
    (let [result (pcore/get-by-id persistence "user" id)]
        (_source->User persistence (:_source result))))

(defn encrypt-user-password
    "Encrypt the user password (one way)"
    [password]
    (sc/encrypt password 16384 8 1))

(defn get-user-by-email
    "get a user by email. Returns nil if not found"
    [persistence email]
    (first (pcore/search-to-record persistence "user" (partial _source->User persistence) :query (esq/match-all) :size 1 :filter {:term {:email email}})))

(defn ^boolean verify-user-password
    "Verify that the password the user has matches the candidate"
    [user password-candidate]
    (let [password (:password user)]
        (if (and password-candidate password)
            (sc/verify password-candidate password)
            false)))

(defn save-user
    "Save a user"
    [persistence user]
    (if (pcore/present? persistence (pcore/get-type user) (:id user))
        (pcore/save persistence user)
        (do
            (let [password (encrypt-user-password (:password user))
                  user (assoc user :password password)]
                (pcore/save persistence user)))))

;; handler functions
(defmethod rpc/rpc-handler [:user :save]
           [system ^Request request]
    "Save the user please"
    (let [persistence (pcore/sub-system system)
          result (save-user persistence (:data request))]
        (pcore/refresh persistence)
        result))

(defmethod rpc/rpc-handler [:user :list]
           [system ^Request request]
    "Give me a list of users please"
    (let [persistence (pcore/sub-system system)]
        (list-users persistence)))

(defmethod rpc/rpc-handler [:user :load]
           [system ^Request request]
    "Load a user by id"
    (let [persistence (pcore/sub-system system)]
        (get-user-by-id persistence (:data request))))