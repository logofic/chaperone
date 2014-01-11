(ns ^{:doc "Manage the web facing part of the application"}
    chaperone.web.core
    (:use [clojure.core.async :only [go <!]]
          [while-let.core])
    (:require [chaperone.rpc :as rpc]
              [org.httpkit.server :as server]
              [environ.core :as env]
              [compojure.core :as comp]
              [compojure.handler :as handler]
              [compojure.route :as route]
              [ring.middleware.cookies :as cookies]
              [selmer.parser :as selmer]
              [dieter.core :as dieter]
              [clojure.edn :as edn])
    )

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:port    (env/env :web-server-port 8080)
                      :dieter  {:engine     :v8
                                :compress   false ; minify using Google Closure Compiler & Less compression
                                :cache-mode :production ; or :production. :development disables cacheing
                                }
                      :clients (atom {})}]
        (assoc system :web sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:web system))

(defn websocket-on-recieve!
    "Returns a handler function for when data is recieved by the websocket"
    [system client]
    (fn [data]
        (let [rpc (rpc/sub-system system)
              request (edn/read-string {:readers (:edn-readers rpc)} data)]
            (rpc/send-request! rpc client request))))

(defn websocket-on-close
    "Returns a handler function for when a websocket is closed"
    [web client]
    (fn [status] (swap! (:clients web) dissoc client)))

(defn websocket-on-connect
    "Handler for when a websocket conenction is made"
    [web request client]
    (println "Connected: " request client)
    (swap! (:clients web) assoc client true))

;;; logic
(defn- websocket-rpc-handler
    "Handle websocket requests"
    [system request]
    (let [web (sub-system system)
          rpc (rpc/sub-system system)]
        (server/with-channel request client
                             (websocket-on-connect web request client)
                             (server/on-close client (websocket-on-close web client))
                             (server/on-receive client (websocket-on-recieve! system client)))))

(defn- create-routes
    [system]
    (let [web (sub-system system)]
        (comp/routes
            (comp/GET "/" [] (selmer/render-file "views/index.html"
                                                 {:less (dieter/link-to-asset "main.less" (:dieter web))}
                                                 {:tag-open \[ :tag-close \]}))
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

(defn start!
    "Start the web server, and get this ball rolling"
    [system]
    (let [web (sub-system system)
          port (:port web)]
        (println "Starting server on port " port)
        (if-not (:server web)
            (assoc system :web
                          (assoc web :server (run-server system)))
            system)))

(defn stop!
    "Oh noes. Stop the server!"
    [system]
    (let [web (sub-system system)]
        (when web
            (when (:server web)
                ((:server web)))))
    system)