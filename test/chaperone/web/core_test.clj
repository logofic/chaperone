(ns chaperone.web.core-test
    (:use [midje.sweet]
          [chaperone.web.core]
          [clojure.core.async :only [timeout <!! put!]])
    (:require [test-helper :as test]
              [chaperone.web.rpc :as rpc]
              [chaperone.crossover.rpc :as x-rpc]
              [chaperone.crossover.user :as x-user]
              [chaperone.persistence.core :as pcore]
              [chaperone.persistence.install :as install]
              [org.httpkit.server :as server]
              [clojure.edn :as edn]
              [chaperone.web.websocket :as ws]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact "Sending a request will result in a response coming back on the same channel"
      (let [channel {:channel true}
            test-user (x-user/new-user "Mark" "Mandel" "email" "password")
            request (x-rpc/new-request :user :save test-user)
            result-chan (timeout 5000)]
          (with-redefs [server/send! (fn [channel data] (put! result-chan [channel data]))]
                       (test/start pcore/start! install/start! rpc/start!)
                       ((ws/websocket-on-recieve! test/system channel) (pr-str request))
                       (let [result (<!! result-chan)
                             rchannel (first result)
                             data (last result)
                             response (edn/read-string {:readers (x-rpc/all-edn-readers)} data)]
                           rchannel => channel
                           (:request response) => request))))