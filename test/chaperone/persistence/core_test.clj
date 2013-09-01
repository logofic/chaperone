(ns chaperone.persistence.core-test
	(:use midje.sweet)
	(:use chaperone.persistence.core)
	(:require [chaperone.user :as user]
						[clj-time.core :as time]
						[clj-time.format :as timef]
						[chaperone.persistence.install :as install]
						[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]
						[clojurewerkz.elastisch.rest.document :as esd]))

;;clean out the index before we begin
(namespace-state-changes (before :facts (esi/delete es-index)
													 ))
(fact
	"Should be no index"
	(esi/exists? es-index) => false
	)

(fact
	"Should be able to store and retrieve a Persistent record"
	(let [test-user (user/new-user "Mark" "Mandel" "email" "password")]
		(install/create-index)
		(create test-user)
		(-> (get-by-id "user" (:id test-user)) :_source :id) => (:id test-user)))

(fact "Should be able to store and retrieve a date"
	(let [test-user (user/new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now))]
		(install/create-index)
		(create test-user)
		(let [result (->> (:id test-user) (get-by-id "user") :_source)]
			(timef/parse date-formatter (:last-logged-in result)) => (:last-logged-in test-user))))