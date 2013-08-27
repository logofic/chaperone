(ns ^{:doc "Persistance (ElasticSearch) installation scripts"}
	chaperone.persistence.install
	(:require [chaperone.persistence.core :as pcore]
						[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]))


(def user-mapping {:user {:properties {:id {:type "string" :store true :index "not_analyzed"}
																				:firstname {:type "string" :store true}
																				:lastname {:type "string" :store true}
																				:password {:type "string" :index "not_analyzed"}
																				:email {:type "string" :store true}
																				:photo {:type "string" :store true :index "not_analyzed"}
																				:last-logged-in {:type "date" :store true}}}})

(defn create-index []
	"Installs the index and require mappings for elasticsearch"
	(let [mappings (merge user-mapping)]
		(esi/create pcore/es-index :mappings mappings)))

