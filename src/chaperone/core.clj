(ns	^{:doc "Actually runs the application server."}
	chaperone.core
	(:require [chaperone.persistence.core :as pcore])
	(:gen-class))

(defn create-system
	"Create the system context, but don't start it"
	[]
	(let [context {}]
		(-> context pcore/create-sub-system)))

(defn -main
	"I don't do a whole lot ... yet."
	[& args]
	(println "Hello, World!" args))