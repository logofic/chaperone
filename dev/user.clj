(ns user
    "Tools for interactive development with the REPL. This file should
    not be included in a production build of the application."
    (:use [midje.repl :only (autotest load-facts)]
          [clojure.pprint :only (pprint)]
          [clojure.repl]
          [clojure.tools.namespace.repl :only (refresh refresh-all)]
          [clojure.tools.trace])
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
    (alter-var-root #'system core/start!))

(defn stop
    "Stops the system if it is currently running, updates the Var
      #'system."
    []
    (alter-var-root #'system core/stop!))

(defn go
    "Initializes and starts the system running."
    []
    (create)
    (start)
    :ready)

(defn reset
    "Stops the system, optionally reloads modified source files, and restarts it."
    []
    (stop)
    (refresh :after 'user/go))

;; helper functions

(defn autotest-focus
    "Only autotest on the focused item"
    []
    (autotest :stop)
    (autotest :filter :focus))

(defn autotest-fast
    "autotest only fast items"
    []
    (autotest :stop)
    (autotest :filter (complement :webdriver)))

(defn load-facts-focus
    "Only load tests under focus"
    []
    (load-facts :filter :focus))

(defn load-facts-fast
    "Only load the facts that are FAST (e.g. non webdriver)"
    []
    (load-facts (complement :webdriver)))