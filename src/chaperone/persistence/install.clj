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
	[system]
	(let [mappings (merge user-mapping)
		  es-index (pcore/get-es-index system)]
		(esi/create es-index :mappings mappings)))