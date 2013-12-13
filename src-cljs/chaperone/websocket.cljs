(ns ^{:doc "The web socket layer for this application"}
    chaperone.websocket
    (:require [cljs.reader :as reader])
    (:use
        [cljs.core.async :only [chan put! <! close!]]
        [chaperone.crossover.rpc :only [new-request new-response edn-readers]])
    (:import (chaperone.crossover.rpc Request Response))
    (:use-macros
        [purnam.core :only [obj !]]
        [cljs.core.async.macros :only [go]]))


;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system host port]
    (let [sub-system {:host                    host
                      :port                    port
                      :request-chan            (chan)
                      :response-chan           (chan)
                      :response-chan-listening (atom false)
                      :rpc-map                 (atom {})
                      :edn-readers             (edn-readers)}]
        (assoc system :websocket sub-system))
    )

(defn sub-system
    "get the persistence system from the global"
    [system]
    (:websocket system))

(defn respond!
    "Sends the final response to the RPC request's channel. Removes the response RPC channel in question after
    putting the response in it."
    [web-sockets ^Response response]

    (let [rpc-map (:rpc-map web-sockets)
          rpc-id (-> response :request :id)
          rpc-chan (get @rpc-map rpc-id)]
        (put! rpc-chan response)
        (swap! rpc-map dissoc rpc-id)
        )
    )

(defn- start-response-chan-listen!
    "Listen to the response chan's channel and route responses appropriately"
    [web-socket]
    (reset! (:response-chan-listening web-socket) true)
    (go
        (while (-> web-socket :response-chan-listening deref)
            (respond! web-socket (reader/read-string (<! (:response-chan web-socket)))))))

(defn start!
    "Start the system"
    [system]
    (doseq [[tag f] (edn-readers)]
        (reader/register-tag-parser! tag f))
    (-> system sub-system start-response-chan-listen!)
    system)

(defn stop!
    "Stop the system"
    [system]
    (let [web-socket (sub-system system)]
        (close! (:request-chan web-socket))
        (close! (:response-chan web-socket))
        (reset! (:response-chan-listening web-socket) false))
    system)

(defn send!
    "Send a rpc request over the websocket channel. Returns the channel that the RPC response will come back to."
    [web-socket ^Request request]
    (let [id (:id request)
          ws-chan (:request-chan web-socket)
          response-chan (chan)
          rpc-map (:rpc-map web-socket)]
        (swap! rpc-map assoc id response-chan)
        (put! ws-chan request)
        (close! ws-chan) ; nothing else is going on, so let's be safe.
        response-chan))