(ns chaperone.core-test
	(:use midje.sweet)
	(:use chaperone.core)
	(:require [chaperone.user :as user]
			  [clojurewerkz.elastisch.rest.document :as esd]))

(fact
	"I am doign something awesome."
	(conj [1 2] 3) => [1 2 3]
	)