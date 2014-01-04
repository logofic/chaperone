(ns ^{:doc "Function tests around user admin"}
    chaperone.functional.admin.user
    (:use [midje.sweet]
          [clj-webdriver.taxi])
    (:require [test-helper :as test]
              [chaperone.persistence.install :as install]
              [clojurewerkz.elastisch.rest.index :as esi]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create)
    (test/start-all)
    (esi/delete @test/es-index)
    (install/create-index test/system))

(defn- teardown!
    []
    (test/stop))

(namespace-state-changes (before :facts (setup!))
                         (after :facts (teardown!)))

(fact "Adding a user to the admin shows up on the list" :webdriver
      (with-driver {:browser :chrome}
                   (implicit-wait 5000)
                   (to "http://localhost:9080")
                   (click "div.navbar a.dropdown-toggle")
                   (click "li.dropdown.open a[href='#/admin/users/add']")

                   (quick-fill-submit {"#first-name" "Mark"} {"#last-name" "Mandel"}
                                      {"#email" "e@e.com"} {"#password" "password"}
                                      {"form button[type='submit']" click})
                   (text "table tbody tr") => "1 Mark Mandel e@e.com"))