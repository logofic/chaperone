(ns chaperone.user-test
	(:use midje.sweet)
	(:use chaperone.user)
	(:require [clj-time.core :as time])
)

;;; helper functions

(defn create-test-user []
  "I create a simple test user"
	(->User 0 "Mark" "Mandel" "password" "email" "photo.jpg" (time/now))
	)

;;; facts

(fact "I can create a user"
	(create-test-user) => truthy
	(:firstname (create-test-user)) => "Mark"
	(:lastname (create-test-user)) => "Mandel"
	(:password (create-test-user)) => "password"
	(:email (create-test-user)) => "email"
	)
