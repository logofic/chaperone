(ns chaperone.rpc-test
    (:require [clojure.edn :as edn])
    (:use [midje.sweet]
          [chaperone.crossover.rpc]
          [chaperone.rpc]
          [clojure.core.async :only [pipe put! <!! timeout]])
    (:require [test-helper :as test]
              [chaperone.crossover.user :as user]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact "Create a new request"
      (let [request (new-request :request-category :request-action {:key "value"})]
          (:id request) => truthy
          (:category request) => :request-category
          (:action request) => :request-action
          (:data request) => {:key "value"}))

(fact "Create a new response"
      (let [request (new-request :request-category :request-action {:key "value"})
            response (new-response request {:value "key"})]
          (:request response) => request
          (:data response) => {:value "key"}))

(fact "EDN test - make sure everything works"
      (let [request (new-request :request-category :request-action {:key "value"})
            response (new-response request {:value "key"})]
          request => (edn/read-string {:readers (edn-readers)} (prn-str request))
          response => (edn/read-string {:readers (edn-readers)} (prn-str response))
          ))

(fact "Make sure the RPC system/subsystem works as expected"
      (:rpc test/system) => truthy
      (:rpc test/system) => (sub-system test/system))

(fact "RPC channel pipeline should take in a request, and spit out a response on the other side."
      (let [rpc (sub-system test/system)
            request-chan (:request-chan rpc)
            response-chan (:response-chan rpc)
            piped-timout (timeout 2000)
            test-user (user/new-user "Mark" "Mandel" "email" "password")
            request (new-request :user :save test-user)]
          (put! request-chan request)
          (pipe response-chan piped-timout)
          (:request (<!! piped-timout)) => request))