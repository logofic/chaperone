(ns ^{:doc "Manage the web facing part of the application"}
	chaperone.web.core
	(require [org.httpkit.server :as server]
			 [environ.core :as env]
			 [compojure.core :as comp]
			 [compojure.handler :as handler]
			 [compojure.route :as route]))

;;; system tools
(defn create-sub-system
	"Create the persistence system. Takes the existing system details"
	[system]
	(let [sub-system {:port (env/env :web-server-port 8080)}]
		(assoc system :web sub-system)))

(defn sub-system
	"get the web system from the global"
	[system]
	(:web system))

;;; logic

(defn index-page
	"Returns the index page"
	[request]
	"Index Page")

;basic configuration
(comp/defroutes site-routes
				(comp/GET "/" [] index-page)
				(route/not-found "<h1>404 OMG</h1>"))

(defn start
	"Start the web server, and get this ball rolling"
	[system]
	(println "Starting server")
	(let [web (sub-system system)
		  port (:port web)]
		(assoc :web
			  (if-not (:server web)
				 (assoc web :server (server/run-server (handler/site #'site-routes) {:port port}))
				  system ))))
(defn stop
	"Oh noes. Stop the server!"
	[system]
	system)

;(run-server (site #'hello-world) {:port 8080})