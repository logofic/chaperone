(ns ^{doc: "RPC utilities for communicating with the server"}
    chaperone.rpc
    (:require [chaperone.websocket :as ws]
              [chaperone.crossover.rpc :as x-rpc]))

(defn send-request
    "Send a request object over the websocket"
    [system category action data]
    (let [request (x-rpc/new-request category action data)
          web-socket (ws/sub-system system)]
        (ws/send! web-socket request)))