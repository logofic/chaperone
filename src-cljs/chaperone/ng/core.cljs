(ns ^{:doc "The angularJS core implementation for the front end of this site"}
	chaperone.ng.core
	)

(defn hello
	[]
	(.log js/console "Hello! v3"))