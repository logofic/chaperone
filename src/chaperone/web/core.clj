(ns ^{:doc "Manage the web facing part of the application"}
    chaperone.web.core
    (:use [clojure.core.async :only [put! go <!]])
    (:require [chaperone.rpc :as rpc]
              [chaperone.crossover.rpc :as x-rpc]
              [org.httpkit.server :as server]
              [environ.core :as env]
              [compojure.core :as comp]
              [compojure.handler :as handler]
              [compojure.route :as route]
              [ring.middleware.cookies :as cookies]
              [selmer.parser :as selmer]
              [dieter.core :as dieter]
              [clojure.edn :as edn])
    (:import (com.google.common.cache CacheBuilder)))

(defn- create-rpc-map
    "Use Google Guava to create the weak concurrent hashmap we want to use for RPC calls"
    []
    (let [builder (CacheBuilder/newBuilder)]
        (doto builder
            (.weakKeys))
        (-> builder .build .asMap)))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:port                 (env/env :web-server-port 8080)
                      :dieter               {:engine     :v8
                                             :compress   false ; minify using Google Closure Compiler & Less compression
                                             :cache-mode :production ; or :production. :development disables cacheing
                                             }
                      :clients              (atom {})
                      :rpc-map              (create-rpc-map)
                      :response-chan-listen (atom false)}]
        (assoc system :web sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:web system))

(defn websocket-on-recieve!
    "Returns a handler function for when data is recieved by the websocket"
    [web rpc channel]
    (fn [data]
        (let [request-chan (:request-chan rpc)
              rpc-map (:rpc-map web)
              request (edn/read-string {:readers (:edn-readers rpc)} data)
              ]
            (.put rpc-map request channel)
            (put! request-chan request))))

(defn websocket-on-close
    "Returns a handler function for when a websocket is closed"
    [web channel]
    (fn [status] (swap! (:clients web) dissoc channel)))

(defn websocket-on-connect
    "Handler for when a websocket conenction is made"
    [web request channel]
    (println "Connected: " request channel)
    (swap! (:clients web) assoc channel true))

;;; logic
(defn- websocket-rpc-handler
    "Handle websocket requests"
    [system request]
    (let [web (sub-system system)
          rpc (rpc/sub-system system)]
        (server/with-channel request channel
                             (websocket-on-connect web request channel)
                             (server/on-close channel (websocket-on-close web channel))
                             (server/on-receive channel (websocket-on-recieve! web rpc channel)))))

(defn- create-routes
    [system]
    (let [web (sub-system system)]
        (comp/routes
            (comp/GET "/" [] (selmer/render-file "views/index.html"
                                                 {:less (dieter/link-to-asset "main.less" (:dieter web))}))
            (comp/GET "/rpc" [] (partial websocket-rpc-handler system))
            (route/resources "/public")
            (route/not-found "<h1>404 OMG</h1>"))))

(defn site
    "Creates a handler specific to what we need in this application. Cookies, but no session"
    [routes]
    (-> routes
        handler/api
        cookies/wrap-cookies))

(defn run-server
    "runs the server, and returns the stop function"
    [system]
    (let [web (sub-system system)
          port (:port web)]
        (server/run-server (-> (site (create-routes system)) (dieter/asset-pipeline (:dieter web))) {:port port})))

(defn start-rpc-response-listen
    "Start listening to the rpc response channel"
    [system]
    (go
        (let [web (sub-system system)
              rpc (rpc/sub-system system)]
            (while (-> web :response-chan-listen deref)
                (let [response (<! (:response-chan rpc))
                      request (:request response)
                      rpc-map (:rpc-map rpc)
                      channel (.get rpc-map request)]
                    (server/send! channel (pr-str response)))))))

(defn start!
    "Start the web server, and get this ball rolling"
    [system]
    (println "Starting server")
    (let [web (sub-system system)
          port (:port web)]
        (reset! (:response-chan-listen web) true)
        (if-not (:server web)
            (assoc system :web
                          (assoc web :server (run-server system)))
            system)))

(defn stop!
    "Oh noes. Stop the server!"
    [system]
    (let [web (sub-system system)]
        (when web
            (reset! (:response-chan-listen web) false)
            (when (:server web)
                ((:server web)))))
    system)