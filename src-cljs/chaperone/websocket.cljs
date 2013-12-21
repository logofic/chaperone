(ns ^{:doc "The web socket layer for this application"}
    chaperone.websocket
    (:require [cljs.reader :as reader])
    (:use
        [cljs.core.async :only [chan put! <! close! timeout]]
        [chaperone.crossover.rpc :only [new-request new-response all-edn-readers]])
    (:import (chaperone.crossover.rpc Request Response))
    (:use-macros
        [purnam.core :only [obj ! ? !>]]
        [cljs.core.async.macros :only [go]]))


;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system host port]
    (let [sub-system {:host                 host
                      :port                 port
                      :request-chan         (chan)
                      :request-chan-listen  (atom false)
                      :response-chan        (chan)
                      :response-chan-listen (atom false)
                      :rpc-map              (atom {})
                      :edn-readers          (all-edn-readers)}]
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
        (close! rpc-chan) ;nothing else is going in.
        (swap! rpc-map dissoc rpc-id)
        )
    )
(defn- start-request-chan-listen!
    "Start listening to requests, and route requests to the websocket.send()"
    [web-socket]
    (reset! (:request-chan-listen web-socket) true)
    (go (while (-> web-socket :request-chan-listen deref)
            (let [socket (:socket web-socket)]
                (.log js/console "Socket is: " socket)
                (.send socket (-> web-socket :request-chan <! pr-str))))))

(defn- start-response-chan-listen!
    "Listen to the response chan's channel and route responses appropriately"
    [web-socket]
    (reset! (:response-chan-listen web-socket) true)
    (go
        (while (-> web-socket :response-chan-listen deref)
            (respond! web-socket (reader/read-string (<! (:response-chan web-socket)))))))

(defn connect-websocket!
    "Create the websocket and connect it up. Returns the configured web socket.
    Puts the value of 'true' in the socket-connected channel when opened"
    [web-socket socket-connected]
    (let [ws-url (str "ws://" (:host web-socket) ":" (:port web-socket) "/rpc")
          socket (js/WebSocket. ws-url)]
        (.log js.console "Connecting to WS: " ws-url)
        (! socket.onopen (fn []
                             (.log js/console "Connected!")
                             (put! socket-connected true)))
        (! socket.onerror (fn [e] (.error js/console "Websocket Error: " e)))
        (! socket.onmessage (fn [e]
                                (.log js/console "On Message: " (? e.data))
                                (put! (:response-chan web-socket) (? e.data))))
        socket))

(defn start!
    "Start the system"
    [system]
    (doseq [[tag f] (all-edn-readers)]
        (reader/register-tag-parser! tag f))
    (let [web-socket (sub-system system)
          socket-connected (timeout 10000)
          socket (connect-websocket! web-socket socket-connected)
          ; overwrite it, so that the actual socket object is available.
          web-socket (assoc web-socket :socket socket)]
        (start-response-chan-listen! web-socket)
        (let [system (assoc system :websocket web-socket)]
            (go (<! socket-connected)
                (start-request-chan-listen! web-socket))
            system)))

(defn stop!
    "Stop the system"
    [system]
    (let [web-socket (sub-system system)]
        (close! (:request-chan web-socket))
        (close! (:response-chan web-socket))
        (reset! (:response-chan-listen web-socket) false)
        (reset! (:response-chan-listen web-socket) false))
    system)

(defn send!
    "Send a rpc request over the websocket channel. Returns the channel that the RPC response will come back to."
    [web-socket ^Request request]
    (let [id (:id request)
          request-chan (:request-chan web-socket)
          result-chan (chan)
          rpc-map (:rpc-map web-socket)]
        (swap! rpc-map assoc id result-chan)
        (put! request-chan request)
        result-chan))