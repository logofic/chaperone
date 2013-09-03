(ns chaperone.persistence.install-test
	(:use midje.sweet)
	(:use chaperone.persistence.install)
	(:require [chaperone.persistence.core :as pcore]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]))

(defn- setup []
	   (esi/delete pcore/es-index)
	   (create-index)
	   )

;;clean out the index before we begin
(namespace-state-changes (before :facts (setup)))

(fact
	"Shall we look at this index of ours?"
	(esi/exists? pcore/es-index) => true
	(-> (esi/get-mapping pcore/es-index "user") :user :properties keys sort) => (-> user-mapping :user :properties keys sort)
	)