(ns chaperone.user
    (:require [chaperone.rpc :as rpc])
    (:import chaperone.crossover.user.User))

(defn save-user
    "Send a user back to the server and save it"
    [system ^User user]
    (rpc/send-request system :user :save user))

(defn list-users
    "List some users for me please"
    [system]
    (rpc/send-request system :user :list {}))

(defn get-user-by-id
    [system id]
    (rpc/send-request system :user :load id))