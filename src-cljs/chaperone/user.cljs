(ns chaperone.user
    (:require [cljs.core.async :refer [chan >! <!]])
    (:import chaperone.crossover.user.User)
    (:use-macros [cljs.core.async.macros :only [go]]))

(defn save-user
    "Send a user back to the server and save it"
    [^User user]
    ;;TODO: Need to actually make this work
    (throw (js/Error "save-user Not implemented")))
