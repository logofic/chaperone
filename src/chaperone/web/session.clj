(ns ^{:doc "Management of the application web sessions"}
    chaperone.web.session
    (:require [cljs-uuid.core :as uuid]))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:websocket-clients (atom {})
                      :loggedin-users    (atom {})}]
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

(defn open-session
    "starts a session for a websocket connection"
    [session cookies client]
    (let [sid (if (:sid cookies)
                  (:sid cookies)
                  (get-in cookies ["sid" :value]))]
        (if-not sid
            (throw (Exception. "SID not present in cookie")))
        (swap! (:websocket-clients session) assoc client sid)))

(defn close-session
    "Closes an existing session"
    [session client]
    (swap! (:websocket-clients session) dissoc client))