(ns chaperone.core-test
  (:use midje.sweet)
	(:use chaperone.core))

(fact
	"I am doign something awesome."
	(conj [1 2] 3) => [1 2 3]
	)