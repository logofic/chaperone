(ns chaperone.rpc-test
    (:use [midje.sweet]
          [chaperone.crossover.rpc])
    (:require [test-helper :as test]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact
    "Create a new request"
    (let [request (new-request "category" "action" {:key "value"})]
        (:id request) => truthy
        (:category request) => "category"
        (:action request) => "action"
        (:data request) => {:key "value"}))

(fact
    "Create a new response"
    (let [request (new-request "category" "action" {:key "value"})
          response (new-response request {:value "key"})]
        (:request response) => request
        (:data response) => {:value "key"}))