(ns chaperone.persistence.core-test
	(:use [midje.sweet]
		  [chaperone.persistence.core])
	(:require [test-helper :as test]
			  [chaperone.user :as user]
			  [clj-time.core :as time]
			  [clj-time.format :as timef]
			  [chaperone.persistence.install :as install]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]
			  [clojurewerkz.elastisch.rest.document :as esd]
			  [clojurewerkz.elastisch.query :as esq]))

;;clean out the index before we begin

(defn- setup!
	   "Provides setup for the tests. Has side effects"
	   []
	   (test/stop)
	   (test/create)
	   (test/start start)
	   (esi/delete @test/es-index))

(namespace-state-changes (before :facts (setup!)))

(fact
	"Should be no index"
	(esi/exists? @test/es-index) => false
	)

(fact
	"Should be able to store and retrieve a Persistent record"
	(let [test-user (user/new-user "Mark" "Mandel" "email" "password")
		  persistence (sub-system test/system)]
		(install/create-index test/system)
		(create persistence test-user)
		(-> (get-by-id persistence "user" (:id test-user)) :_source :id) => (:id test-user)))

(fact "Should be able to store and retrieve a date"
	  (let [test-user (user/new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now))
			persistence (sub-system test/system)]
		  (install/create-index test/system)
		  (create persistence test-user)
		  (let [result (->> (:id test-user) (get-by-id persistence "user") :_source)]
			  (parse-string-date persistence (:last-logged-in result)) => (:last-logged-in test-user))))

(fact "Should be able to store and retrieve a date, even if it's nil"
	  (let [test-user (user/new-user "Mark" "Mandel" "email" "password")
			persistence (sub-system test/system)]
		  (install/create-index test/system)
		  (create persistence test-user)
		  (let [result (->> (:id test-user) (get-by-id persistence "user") :_source)]
			  (parse-string-date persistence (:last-logged-in result)) => (:last-logged-in test-user))))

(defn- es-result-to-id [result]
	   "Convert the elastic search results to a list of ids, for easy comparison"
	   (mapv (fn [item] (-> item :_source :id)) (-> result :hits :hits)))

(fact "Should be able to query for data"
	  (let [test-user1 (user/new-user "Mark" "Mandel" "email" "password")
			test-user2 (user/new-user "ZAardvark" "ZAbigail" "email" "password")
			persistence (sub-system test/system)]
		  (install/create-index test/system)
		  (create persistence test-user1)
		  (create persistence test-user2)
		  (esi/refresh @test/es-index)
		  (es-result-to-id (search persistence "user" :query (esq/match-all) :sort {:lastname "asc"})) => [(:id test-user1) (:id test-user2)]
		  (es-result-to-id (search persistence "user" :query (esq/match-all) :sort {:lastname "desc"})) => [(:id test-user2) (:id test-user1)]))

(fact "Should be able to transform search data to appropriate defrecords" :focus
	  (let [test-user1 (user/new-user "Mark" "Mandel" "email" "password")
			test-user2 (user/new-user "ZAardvark" "ZAbigail" "email" "password")
			_source->User (partial user/_source->User test/system)
			persistence (sub-system test/system)]
		  (install/create-index test/system)
		  (doto persistence
			  (create test-user1)
			  (create test-user2))
		  (esi/refresh @test/es-index)
		  (search-to-record persistence "user" _source->User :query (esq/match-all) :sort {:lastname "asc"}) => [test-user1 test-user2]
		  (search-to-record persistence "user" _source->User :query (esq/match-all) :sort {:lastname "desc"}) => [test-user2 test-user1]))