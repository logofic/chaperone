(ns chaperone.persistence.install-test
	(:use midje.sweet)
	(:use chaperone.persistence.install)
	(:require [test-helper :as test]
			  [chaperone.persistence.core :as pcore]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]))

(defn- setup!
	   "Provides setup for the tests. Has side effects"
	   []
	   (test/stop)
	   (test/create)
	   (test/start pcore/start)
	   (esi/delete @test/es-index)
	   (create-index test/system))

;;clean out the index before we begin
(namespace-state-changes (before :facts (setup!)))

(fact :focus
	"Shall we look at this index of ours?"
	(esi/exists? @test/es-index) => true
	(-> (esi/get-mapping @test/es-index "user") :user :properties keys sort) => (-> user-mapping :user :properties keys sort)
	)