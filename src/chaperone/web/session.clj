(ns ^{:doc "Management of the application web sessions"}
        chaperone.web.session
    (:require [cljs-uuid.core :as uuid]))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:sessions (atom {})}]
        (assoc system :session sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:session system))

(defn manage-session-cookies
    "Pass in the map for cookies, and passes back what is needed for session management"
    [cookies]
    (if (:sid cookies)
        cookies
        (assoc cookies :sid (uuid/make-random-string))))