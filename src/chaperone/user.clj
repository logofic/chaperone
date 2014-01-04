(ns ^{:doc "System user and user management"}
    chaperone.user
    (:use [chaperone.crossover.user])
    (:import chaperone.crossover.user.User
             chaperone.crossover.rpc.Request)
    (:require [chaperone.persistence.core :as pcore]
              [clojurewerkz.elastisch.query :as esq]
              [clojurewerkz.scrypt.core :as sc]
              [chaperone.rpc :as rpc]))

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

(defn save-user
    "Save a user"
    [persistence user]
    (if (pcore/present? persistence (pcore/get-type user) (:id user))
        (pcore/save persistence user)
        (do
            (let [password (sc/encrypt (:password user) 16384 8 1)
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