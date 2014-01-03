(ns test-helper
    (:require [chaperone.core :as core]
              [chaperone.persistence.core :as pcore]
              [environ.core :as env]))

(def es-index (atom 0))

(def system
    "A Var containing an object representing the application under
     development."
    nil)

(defn create
    "Creates and initializes the system under development in the Var
      #'system. For convenience, swap the es-index while we're in here"
    []
    (with-redefs [env/env (merge env/env {:elasticsearch-index "test_chaperone" :web-server-port 9080})]
                 (alter-var-root #'system (constantly (core/create-system)))
                 (swap! es-index (constantly (pcore/get-es-index system)))))

(defn start
    "Starts the system, reducing the passed in start functions, so you can cherry pick what
    sub systems you want to actually start u in tests"
    [& args]
    (alter-var-root #'system
                    (fn [s] (reduce (fn [memo f] (apply f [memo])) s args))))

(defn stop
    "Stops the system if it is currently running, updates the Var
      #'system."
    []
    (alter-var-root #'system core/stop!))