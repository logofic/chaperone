(ns ^{:doc "Manage the web facing part of the application"}
	chaperone.web.core
	(require [org.httpkit.server :as server]
			 [environ.core :as env]
			 [compojure.core :as comp]
			 [compojure.handler :as handler]
			 [compojure.route :as route]
			 [selmer.parser :as selmer]
			 [dieter.core :as dieter]))

;;; system tools
(defn create-sub-system
	"Create the persistence system. Takes the existing system details"
	[system]
	(let [sub-system {:port   (env/env :web-server-port 8080)
					  :dieter {:engine     :rhino ; defaults to :rhino; :v8 is much much faster
							   :compress   false ; minify using Google Closure Compiler & Less compression
												  ;:asset-roots ["resources/assets"] ; must have a folder called 'assets'. Searched for assets in the order listed.
												  ;:cache-root  "resources/assets-cache" ; compiled assets are cached here
							   :cache-mode :development ; or :production. :development disables cacheing
							   :log-level  :normal} ; or :quiet
					  }]
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

(def dieter-config
	 {
		 :engine     :v8 ; defaults to :rhino; :v8 is much much faster
		 :compress   false ; minify using Google Closure Compiler & Less compression
		 :cache-mode :production ; or :production. :development disables cacheing
		 })

;basic configuration
(comp/defroutes site-routes
				(comp/GET "/" [] (selmer/render-file "views/index.html"
													 {:foo  "bar",
													  :less (dieter/link-to-asset "main.less" dieter-config)}))
				(route/not-found "<h1>404 OMG</h1>"))

(defn run-server
	"runs the server, and returns the stop function"
	[web port]
	(server/run-server (-> (-> #'site-routes handler/site) (dieter/asset-pipeline dieter-config)) {:port port}))

(defn start
	"Start the web server, and get this ball rolling"
	[system]
	(println "Starting server")
	(let [web (sub-system system)
		  port (:port web)]
		(if-not (:server web)
			(assoc system :web
						  (assoc web :server (run-server web port)))
			system)))

(defn stop
	"Oh noes. Stop the server!"
	[system]
	(let [web (sub-system system)]
		(if (:server web)
			((:server web))))
	system)

;(run-server (site #'hello-world) {:port 8080})