(ns chaperone.user-test
	(:use [midje.sweet]
		  [chaperone.user])
	(:require [clj-time.core :as time]
			  [clojurewerkz.elastisch.rest.index :as esi]
			  [chaperone.persistence.install :as install]
			  [chaperone.persistence.core :as pcore]))

;;; facts

(fact
	"Better constructor works"
	(let [test-user (new-user "Mark" "Mandel" "email" "password")]
		(:id test-user) => truthy
		(:firstname test-user) => "Mark"
		(:lastname test-user) => "Mandel"
		(:password test-user) => "password"
		(:email test-user) => "email"
		(:photo test-user) => nil
		(:last-logged-in test-user) => nil
		)
	)

(fact
	"Test optional arguments work"
	(let [test-user (new-user "Mark" "Mandel" "email" "password" :photo "photo" :last-logged-in time/now)]
		(:id test-user) => truthy
		(:firstname test-user) => "Mark"
		(:photo test-user) => "photo"
		(:last-logged-in test-user) => truthy
		)
	)

(fact
	"Persistance methods work correctly"
	(let [test-user (new-user "Mark" "Mandel" "email" "password")]
		(pcore/get-type test-user) => "user"))

(fact
	"Test if the _source->User works properly"
	(esi/delete pcore/es-index)
	(install/create-index)
	(let [test-user (new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now) :photo "photo.jpg")]
		(pcore/create test-user)
		(let [result (-> (pcore/get-by-id "user" (:id test-user)) :_source _source->User)]
			(doseq [key (keys result)]
				(key test-user) => (key result))
			)
		))

(fact "Be able to list all users"
	  (let [test-user1 (new-user "Mark" "Mandel" "email" "password")
			test-user2 (new-user "ZAardvark" "ZAbigail" "email" "password")]
		  (esi/delete pcore/es-index)
		  (install/create-index)
		  (pcore/create test-user1)
		  (pcore/create test-user2)
		  (esi/refresh pcore/es-index)
		  (list-users) => [test-user1 test-user2]))