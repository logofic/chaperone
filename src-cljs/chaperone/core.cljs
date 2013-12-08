(ns ^{:doc "Central core of the system. Lots of setting up of the system and starting / stopping it"}
    chaperone.core
    (:require [chaperone.websocket :as ws]))

;; System startup and shutdown
(defn create-system
    "Create the system context, but don't start it"
    [host port]
    (let [context {}]
        (-> context (ws/create-sub-system host port))))

(defn start!
    "Starts the system"
    [system]
    (.log js/console "Starting the system")
    (-> system ws/start!))

(defn stop!
    "Stop the system"
    [system]
    (.log js/console "Stopping the system")
    (-> system ws/stop!))