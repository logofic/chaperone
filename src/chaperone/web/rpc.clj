(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.web.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close! put!]]
          [while-let.core])
    (:require [org.httpkit.server :as server])
    (:import [chaperone.crossover.rpc Request]
             [com.google.common.cache CacheBuilder]))

;; system

(defn- create-rpc-map
    "Use Google Guava to create the weak concurrent hashmap we want to use for RPC calls"
    []
    (let [builder (CacheBuilder/newBuilder)]
        (doto builder
            (.weakKeys))
        (-> builder .build .asMap)))

(defn create-sub-system
    "Create the RPC subsystem"
    [system]
    (let [sub-system {:edn-readers   (all-edn-readers)
                      :request-chan  (chan)
                      :response-chan (chan)
                      :rpc-map       (create-rpc-map)}]
        (assoc system :rpc sub-system)))

(defn sub-system
    "RPC sub system"
    [system]
    (:rpc system))

(defmulti rpc-handler
          "Is the function for a given request of type [:category :action] and returns the data to be sent back in the response"
          (fn [system ^Request request] [(:category request) (:action request)]))

(defn run-rpc-request
    "Actually run the function for a request"
    [system ^Request request]
    ;; on stop, the request can be nil. Ignore that
    (when request
        (rpc-handler system request)))

(defn- put-client!
    "Starts a request, and stores the websocket client for later retrieval"
    [rpc ^Request request client]
    (.put (:rpc-map rpc) request client))

(defn get-client
    "Retrieve the the client for a specific request"
    [rpc ^Request request]
    (.get (:rpc-map rpc) request))

(defn- remove-client!
    "Remove the request from the request map. Returns the websocket client the RPC request originated from."
    [rpc ^Request request]
    (.remove (:rpc-map rpc) request))

(defn run-client-rpc-request!
    "Runs the rpc request against the connected client, so we know who to send the result back to"
    [system client request]
    (let [rpc (sub-system system)]
        (put-client! rpc request client)
        (new-response request (run-rpc-request system request))))

(defn- start-rpc-request-listen!
    "Start listening to the rpc request channel, and process it"
    [system]
    (let [rpc (sub-system system)]
        (go (while-let [packet (<! (:request-chan rpc))]
                       (when packet
                           (let [client (:client packet)
                                 request (:data packet)]
                               (>! (:response-chan rpc) (run-client-rpc-request! system client request))))))))

(defn- start-rpc-response-listen
    "Start listening to the rpc response channel"
    [system]
    (let [rpc (sub-system system)]
        (go
            (while-let [response (<! (:response-chan rpc))]
                       (let [request (:request response)
                             client (remove-client! rpc request)]
                           (when (and response client)
                               (server/send! client (pr-str response))))))))

(defn start!
    "Start the rpc system"
    [system]
    (start-rpc-request-listen! system)
    (start-rpc-response-listen system)
    system)

(defn stop!
    "Stop the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (when rpc
            (close! (:request-chan rpc))
            (close! (:response-chan rpc))))
    system)

(defn send-request!
    "Sends a request to the RPC mechanism"
    [rpc client ^Request request]
    (let [request-chan (:request-chan rpc)]
        (put! request-chan {:client client :data request})))
