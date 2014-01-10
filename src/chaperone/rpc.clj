(ns ^{:doc "RPC mechanisms for the server side."}
    chaperone.rpc
    (:use [chaperone.crossover.rpc]
          [clojure.core.async :only [go >! <! chan close!]]
          [while-let.core])
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

(defn start!
    "Start the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (go (while-let [request (<! (:request-chan rpc))]
                       (let [data (run-rpc-request system request)]
                           (>! (:response-chan rpc) (new-response request data))))))
    system)

(defn stop!
    "Stop the rpc system"
    [system]
    (let [rpc (sub-system system)]
        (when rpc
            (close! (:request-chan rpc))
            (close! (:response-chan rpc))))
    system)

(defn put-client!
    "Starts a request, and stores the websocket client for later retrieval"
    [rpc ^Request request client]
    (.put (:rpc-map rpc) request client))

(defn get-client!
    "After putting a client in for a request. Returns the websocket client the RPC request originated from."
    [rpc ^Request request]
    (.remove (:rpc-map rpc) request))