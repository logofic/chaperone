(ns chaperone.core-test
	(:use midje.sweet)
	(:use chaperone.core)
	(:require [chaperone.user :as user]
			  [clojurewerkz.elastisch.rest.document :as esd]))

(fact
	"I can create a system. Check me out."
	(create-system) => truthy
	)