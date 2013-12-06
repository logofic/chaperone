(ns ^{:doc "The web socket layer for this application"}
    chaperone.websocket
    (:require [cljs.reader :as reader])
    (:use
        [cljs.core.async :only [chan put! !<]]
        [chaperone.crossover.rpc :only [new-request new-response edn-readers]])
    (:import (chaperone.crossover.rpc Request Response))
    (:use-macros
        [purnam.core :only [obj !]]
        [cljs.core.async.macros :only [go]]))


;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system host port]
    (let [sub-system {:host                   host
                      :port                   port
                      :request-chan           (chan)
                      :response-chan          (chan)
                      :reponse-chan-listening (atom false)
                      :rpc-map                (atom {})
                      :edn-readers            (edn-readers)}]
        (assoc system :websocket sub-system))
    )

(defn sub-system
    "get the persistence system from the global"
    [system]
    (:websocket system))

(defn respond
    "Sends the final response to the RPC request's channel"
    [web-sockets ^Response response]
    ;;TODO: Need to actually make this work
    )

(defn- start-response-chan-listen!
    "Listen to the response chan's channel and route responses appropriately"
    [web-socket]
    (reset! (:reponse-chan-listening web-socket) true)
    (go
        (while (-> web-socket :response-chan-listening deref)
            (respond web-socket (reader/read-string (!< chan))))))

(defn start
    "Start the system"
    [system]
    ;;TODO: register each tag with the required map function: https://coderwall.com/p/3xqr7q
    (-> system sub-system start-response-chan-listen!))


(defn send!
    "Send a rpc request over the websocket channel. Returns the channel that the RPC response will come back to."
    [web-socket ^Request request]
    (let [id (:id request)
          ws-chan (:request-chan web-socket)
          response-chan (chan)
          rpc-map (:rpc-map web-socket)]
        (reset! rpc-map (assoc @rpc-map id response-chan))
        (put! ws-chan request)
        response-chan))