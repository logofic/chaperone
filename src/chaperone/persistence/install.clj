(ns ^{:doc "Persistance (ElasticSearch) installation scripts"}
	chaperone.persistence.install
	(:require [chaperone.persistence.core :as pcore]
						[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]))


(def user-mappings {:user {:properties {:id {:type "string" :store "yes" :index "not_analyzed"}
																				:firstname {:type "string" :store "yes"}
																				:lastname {:type "string" :store "yes"}
																				:password {:type "string" :index "not_analyzed"}
																				:email {:type "string" :store "yes"}
																				:photo {:type "string" :store "yes" :index "not_analyzed"}
																				:last-logged-in {:type "date" :store "yes"}}}})

(defn create-index []
	"Installs the index and require mappings for elasticsearch"
	(let [mappings (merge user-mappings)]
		(esi/create pcore/es-index :mappings mappings)))

