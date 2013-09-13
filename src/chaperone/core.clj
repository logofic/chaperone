(ns	^{:doc "Actually runs the application server."}
	chaperone.core
	(:gen-class))

(defn create-system
	"Create the system context, but don't start it"
	[]
	(let [context {}]
		context))

(defn -main
	"I don't do a whole lot ... yet."
	[& args]
	(println "Hello, World!" args))