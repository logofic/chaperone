(ns ^{:doc "Session management"}
    chaperone.session
    (:require [chaperone.rpc :as rpc]))

(defn login
    "Log the user in"
    [system email password]
    (rpc/send-request system :account :login {:email email :password password}))

(defn logout
    "Log the user out"
    [system]
    (rpc/send-request system :account :logout {}))

(defn current-user
    "Load the current user up"
    [system]
    (rpc/send-request system :account :current {}))