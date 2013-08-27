(ns ^{:doc "Core functionality for persistance"}
	chaperone.persistence.core
	(:require [environ.core :as env]
						[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]))

;;; set default connection to elastic search
(esr/connect! (env/env :elasticsearch-url ))

(def es-index
	"The index that we store the data against in elastic search"
	(env/env :elaticsearch-index ))

(defprotocol Persistent
	"Protocol for encapsulationg common persistence functions"
	)