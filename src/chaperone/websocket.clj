(ns ^{:doc "Manages the websocket processing"}
    chaperone.websocket
    (:require [chaperone.rpc :as rpc]
              [clojure.edn :as edn]))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:clients (atom {})}]
        (assoc system :websocket sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:websocket system))

(defn websocket-on-recieve!
    "Returns a handler function for when data is recieved by the websocket"
    [system client]
    (fn [data]
        (let [rpc (rpc/sub-system system)
              request (edn/read-string {:readers (:edn-readers rpc)} data)]
            (rpc/send-request! rpc client request))))

(defn websocket-on-close!
    "Returns a handler function for when a websocket is closed"
    [ws client]
    (fn [status] (swap! (:clients ws) dissoc client)))

(defn websocket-on-connect!
    "Handler for when a websocket conenction is made"
    [ws request client]
    (println "Connected: " request client)
    (swap! (:clients ws) assoc client true))