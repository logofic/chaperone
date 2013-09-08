(ns ^{:doc "Persistance (ElasticSearch) installation scripts"}
	chaperone.persistence.install
	(:require [chaperone.persistence.core :as pcore]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]))


(def user-mapping {:user {:properties {:firstname      {:type "string"}
									   :lastname       {:type "string"}
									   :password       {:type "string" :index "not_analyzed"}
									   :email          {:type "string"}
									   :photo          {:type "string" :index "not_analyzed"}
									   :last-logged-in {:type "date", :format "date_time"}}}})

(defn create-index
	"Installs the index and require mappings for elasticsearch"
	[]
	(let [mappings (merge user-mapping)]
		(esi/create pcore/es-index :mappings mappings)))

