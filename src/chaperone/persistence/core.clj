(ns ^{:doc "Core functionality for persistance"}
	chaperone.persistence.core
	(:require [environ.core :as env]
						[clojurewerkz.elastisch.rest :as esr]))

;;; set default connection to elastic search
(esr/connect! (env/env :elasticsearch-url ))

(def es-index
	"The index that we store the data against in elastic search"
	(env/env :elaticsearch-index ))

(defn create-index-if-not-exists [index mapping-callback]
	"Looks for an index, if it doesn't exist, then calls the mapping-callback to get the create the map for the configuration"
	)

(defprotocol Persistent
	"Protocol for encapsulationg common persistence functions"
	)