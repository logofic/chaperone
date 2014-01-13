(ns ^{:doc "Management of the application web sessions"}
        chaperone.web.session
    (:require [cljs-uuid.core :as uuid]))

(defn manage-session-cookies
    "Pass in the map for cookies, and passes back what is needed for session management"
    [cookies]
    (if (:sid cookies)
        cookies
        (assoc cookies :sid (uuid/make-random-string))))