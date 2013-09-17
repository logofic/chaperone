(ns user
	"Tools for interactive development with the REPL. This file should
	not be included in a production build of the application."
	(:use [midje.repl :only (autotest load-facts)]
		  [clojure.pprint :only (pprint)]
		  [clojure.repl]
		  [clojure.tools.namespace.repl :only (refresh refresh-all)])
	(:require [chaperone.core :as core]))

;; system init functions

(def system
	 "A Var containing an object representing the application under
	  development."
	 nil)

(defn create
	"Creates and initializes the system under development in the Var
	  #'system."
	[]
	(alter-var-root #'system (constantly (core/create-system)))
	)

(defn start
	"Starts the system running, updates the Var #'system."
	[]
	(alter-var-root #'system core/start)
	)

(defn stop
	"Stops the system if it is currently running, updates the Var
	  #'system."
	[]
	;; TODO
	)

(defn go
	"Initializes and starts the system running."
	[]
	(create)
	(start)
	:ready)

(defn reset
	"Stops the system, optionally reloads modified source files, and restarts it."
	([]
	 (reset true))
	([do-refresh]
	 (stop)
	 (if do-refresh
		 (refresh :after 'user/go)
		 (go))))

;; helper functions

(defn autotest-focus
	"Only autotest on the focused item"
	[]
	(autotest :stop)
	(autotest :filter :focus))

(defn load-facts-focus
	"Only load tests under focus"
	[]
	(load-facts :filter :focus))

;
;(defn stop
;	"Shuts down and destroys the current development system."
;	[]
;	(alter-var-root #'system
;					(fn [s] (when s (system/stop s)))))
;