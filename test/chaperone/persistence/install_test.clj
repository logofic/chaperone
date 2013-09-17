(ns chaperone.persistence.install-test
	(:use midje.sweet)
	(:use chaperone.persistence.install)
	(:require [user :as dev]
			  [chaperone.persistence.core :as pcore]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]))

(def local-es-index (atom 0))

(defn- setup!
	   "Provides setup for the tests. Has side effects"
	   []
	   (dev/reset false)
	   (swap! local-es-index (constantly (pcore/get-es-index dev/system)))
	   (esi/delete @local-es-index)
	   (create-index))

;;clean out the index before we begin
(namespace-state-changes (before :facts (setup!)))

(fact
	"Shall we look at this index of ours?"
	(esi/exists? pcore/es-index) => true
	(-> (esi/get-mapping pcore/es-index "user") :user :properties keys sort) => (-> user-mapping :user :properties keys sort)
	)