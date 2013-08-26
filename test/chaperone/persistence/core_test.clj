(ns chaperone.persistence.core-test
	(:use midje.sweet)
	(:use chaperone.persistence.core)
	(:require [clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]))

;;clean out the index before we begin
(namespace-state-changes (before :facts
													 (esi/delete es-index)
													 ))

(fact
	"I am doign something awesome."
	(conj [1 2] 3) => [1 2 3]
	)