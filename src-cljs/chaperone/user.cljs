(ns chaperone.user
    (:require [chaperone.crossover.rpc :as rpc]
              [chaperone.websocket :as ws])
    (:use [cljs.core.async :only [chan >! <!]])
    (:import chaperone.crossover.user.User)
    (:use-macros [cljs.core.async.macros :only [go]]))

(defn save-user
    "Send a user back to the server and save it"
    [system ^User user]
    (let [request (rpc/new-request :user :save user)
          web-socket (ws/sub-system system)]
        (ws/send! web-socket request)))
