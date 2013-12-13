(ns ^{:doc "Manage the web facing part of the application"}
    chaperone.web.core
    (:use [clojure.core.async :only [put!]])
    (:require [chaperone.rpc :as rpc]
              [chaperone.crossover.rpc :as x-rpc]
              [org.httpkit.server :as server]
              [environ.core :as env]
              [compojure.core :as comp]
              [compojure.handler :as handler]
              [compojure.route :as route]
              [selmer.parser :as selmer]
              [dieter.core :as dieter]
              [clojure.edn :as edn]
              ))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:port    (env/env :web-server-port 8080)
                      :dieter  {:engine     :v8
                                :compress   false ; minify using Google Closure Compiler & Less compression
                                :cache-mode :production ; or :production. :development disables cacheing
                                }
                      :clients (atom {})
                      }]
        (assoc system :web sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:web system))

;;; logic
(defn- websocket-rpc-handler
    "Handle websocket requests"
    [system request]
    (let [web (sub-system system)
          rpc (rpc/sub-system system)
          request-chan (:request-chan rpc)]
        (server/with-channel request channel
                             (println "Connected: " request channel)
                             (swap! (:clients web) assoc channel true)
                             (server/on-close channel (fn [status] (swap! (:clients web) dissoc channel)))
                             (server/on-receive channel (fn [data] (put! request-chan (edn/read-string (:edn-readers rpc) data))))
                             )))

(defn- create-routes
    [system]
    (let [web (sub-system system)]
        (comp/routes
            (comp/GET "/" [] (selmer/render-file "views/index.html"
                                                 {:less (dieter/link-to-asset "main.less" (:dieter web))}))
            (comp/GET "/rpc" [] (partial websocket-rpc-handler system))
            (route/resources "/public")
            (route/not-found "<h1>404 OMG</h1>"))))

(defn run-server
    "runs the server, and returns the stop function"
    [system]
    (let [web (sub-system system)
          port (:port web)]
        (server/run-server (-> (handler/site (create-routes system)) (dieter/asset-pipeline (:dieter web))) {:port port})))

(defn start!
    "Start the web server, and get this ball rolling"
    [system]
    (println "Starting server")
    (let [web (sub-system system)
          port (:port web)]
        (if-not (:server web)
            (assoc system :web
                          (assoc web :server (run-server system)))
            system)))

(defn stop!
    "Oh noes. Stop the server!"
    [system]
    (let [web (sub-system system)]
        (if (:server web)
            ((:server web))))
    system)