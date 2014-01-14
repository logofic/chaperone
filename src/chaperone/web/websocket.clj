(ns ^{:doc "Manages the websocket processing"}
    chaperone.web.websocket
    (:use
        [clojure.pprint :only [pprint]])
    (:require [chaperone.web.rpc :as rpc]
              [chaperone.web.session :as session]
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
    [system client]
    (fn [status] (session/close-session (session/sub-system system) client)))

(defn websocket-on-connect!
    "Handler for when a websocket conenction is made"
    [system request client]
    (println "Connected - Request: ")
    (pprint (:cookies request))
    (session/open-session (session/sub-system system) (:cookies request) client))