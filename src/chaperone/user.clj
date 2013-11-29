(ns ^{:doc "System user and user management"}
    chaperone.user
    (:use [chaperone.crossover.user])
    (:import chaperone.crossover.user.User)
    (:require [chaperone.persistence.core :as pcore]
              [clojurewerkz.elastisch.query :as esq]))

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