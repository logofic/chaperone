(ns chaperone.user-test
	(:use [midje.sweet]
				[chaperone.user]
				[chaperone.persistence.core :only (get-type)])
	(:require [clj-time.core :as time]))

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
		(get-type test-user) => "user"))

