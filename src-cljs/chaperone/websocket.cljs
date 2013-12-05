(ns ^{:doc "The web socket layer for this application"}
    chaperone.websocket
    (:use
        [cljs.core.async :only [chan put!]]
        [chaperone.crossover.rpc :only [new-request new-response]])
    (:import (chaperone.crossover.rpc Request Response))
    (:use-macros
        [purnam.core :only [obj !]]
        [cljs.core.async.macros :only [go]]))


;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system host port]
    (let [sub-system {:host    host
                      :port    port
                      :chan    (chan)
                      :rpc-map (atom {})}]
        (assoc system :websocket sub-system))
    )

(defn sub-system
    "get the persistence system from the global"
    [system]
    (:websocket system))

(defn send!
    "Send a rpc request over the websocket channel. Returns the channel that the RPC response will come back to."
    [web-socket ^Request request]
    (let [id (:id request)
          ws-chan (:chan web-socket)
          response-chan (chan)
          rpc-map (:rpc-map web-socket)]
        (reset! rpc-map (assoc @rpc-map id response-chan))
        (put! ws-chan request)
        response-chan))