(ns chaperone.web.core-test
    (:use [midje.sweet]
          [chaperone.web.core])
    (:require [test-helper :as test]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact "Connecting a websocket should store the channel"
      (let [web (sub-system test/system)
            channel {:channel true}
            request {:request true}
            clients (:clients web)]
          (websocket-on-connect web request channel)
          (count @clients) => 1
          (first @clients) => [channel true]))

(fact "Disconnecting a websocket should remove the channel" :focus
      (let [web (sub-system test/system)
            channel {:channel true}
            request {:request true}
            clients (:clients web)]
          (websocket-on-connect web request channel)
          (count @clients) => 1
          ;;returns a handler function. we need to call it.
          ((websocket-on-close web channel) "test")
          (count @clients) => 0))