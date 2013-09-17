(ns chaperone.persistence.core-test
	(:use [midje.sweet]
		  [chaperone.persistence.core])
	(:require [user :as dev]
			  [chaperone.user :as user]
			  [chaperone.core :as core]
			  [clj-time.core :as time]
			  [clj-time.format :as timef]
			  [chaperone.persistence.install :as install]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]
			  [clojurewerkz.elastisch.rest.document :as esd]
			  [clojurewerkz.elastisch.query :as esq]))

;;clean out the index before we begin

(def local-es-index (atom 0))

(defn- setup!
	   "Provides setup for the tests. Has side effects"
	   []
	   (dev/reset false)
	   (swap! local-es-index (constantly (get-es-index dev/system)))
	   (esi/delete @local-es-index))

(namespace-state-changes (before :facts (setup!)))

(fact :focus
	  "Should be no index"
	  (esi/exists? @local-es-index) => false
	  )

(fact
	"Should be able to store and retrieve a Persistent record"
	(let [test-user (user/new-user "Mark" "Mandel" "email" "password")]
		(install/create-index dev/system)
		(create test-user)
		(-> (get-by-id "user" (:id test-user)) :_source :id) => (:id test-user)))

(fact "Should be able to store and retrieve a date"
	  (let [test-user (user/new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now))]
		  (install/create-index dev/system)
		  (create test-user)
		  (let [result (->> (:id test-user) (get-by-id "user") :_source)]
			  (parse-string-date (:last-logged-in result)) => (:last-logged-in test-user))))

(fact "Should be able to store and retrieve a date, even if it's nil" :focus
	  (let [test-user (user/new-user "Mark" "Mandel" "email" "password")]
		  (install/create-index dev/system)
		  (create test-user)
		  (let [result (->> (:id test-user) (get-by-id "user") :_source)]
			  (parse-string-date (:last-logged-in result)) => (:last-logged-in test-user))))

(defn- es-result-to-id [result]
	   "Convert the elastic search results to a list of ids, for easy comparison"
	   (mapv (fn [item] (-> item :_source :id)) (-> result :hits :hits)))

(fact "Should be able to query for data"
	  (let [test-user1 (user/new-user "Mark" "Mandel" "email" "password")
			test-user2 (user/new-user "ZAardvark" "ZAbigail" "email" "password")]
		  (install/create-index dev/system)
		  (create test-user1)
		  (create test-user2)
		  (esi/refresh @local-es-index)
		  (es-result-to-id (search "user" :query (esq/match-all) :sort {:lastname "asc"})) => [(:id test-user1) (:id test-user2)]
		  (es-result-to-id (search "user" :query (esq/match-all) :sort {:lastname "desc"})) => [(:id test-user2) (:id test-user1)]))

(fact "Should be able to transform search data to appropriate defrecords"
	  (let [test-user1 (user/new-user "Mark" "Mandel" "email" "password")
			test-user2 (user/new-user "ZAardvark" "ZAbigail" "email" "password")]
		  (install/create-index dev/system)
		  (create test-user1)
		  (create test-user2)
		  (esi/refresh @local-es-index)
		  (search-to-record "user" user/_source->User :query (esq/match-all) :sort {:lastname "asc"}) => [test-user1 test-user2]
		  (search-to-record "user" user/_source->User :query (esq/match-all) :sort {:lastname "desc"}) => [test-user2 test-user1]))