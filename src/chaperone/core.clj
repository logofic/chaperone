(ns ^{:doc "Actually runs the application server."}
    chaperone.core
    (:require [chaperone.persistence.core :as persist]
              [chaperone.persistence.install :as install]
              [chaperone.web.core :as web]
              [chaperone.rpc :as rpc])
    (:gen-class))

(defn create-system
    "Create the system context, but don't start it"
    []
    (let [context {}]
        (-> context persist/create-sub-system
            rpc/create-sub-system
            web/create-sub-system)))

(defn start!
    "Starts the system"
    [system]
    (println "user/Starting the system")
    (-> system install/start! persist/start! web/start! rpc/start!))

(defn stop! "stop the system"
    [system]
    (-> system web/stop! rpc/stop!))

(defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (println "Hello, World!" args))