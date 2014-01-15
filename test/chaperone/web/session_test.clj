(ns ^{:doc "Management of the application web sessions"}
    chaperone.web.session-test
    (:use [midje.sweet]
          [chaperone.web.session])
    (:require [test-helper :as test]
              [cljs-uuid.core :as uuid]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact "Cookie should have an identified if it doesn't have one already"
      (-> {} manage-session-cookies :sid) => truthy
      (let [cookies (manage-session-cookies {})
            sid (:sid cookies)]
          (-> cookies manage-session-cookies :sid) => sid))

(fact "Exception should be thrown if there is no sid in the cookie"
      (let [session (sub-system test/system)]
          (open-session! session {} {}) => (throws Exception "SID not present in cookie")))

(fact "UUID should be stored against the client when the session opens"
      (let [session (sub-system test/system)
            websocket-clients (:websocket-clients session)
            client {:client true}
            cookies {"sid" {:value (uuid/make-random-string)}}]
          (open-session! session cookies client)
          (get @websocket-clients client) => (get-in cookies ["sid" :value])))

(fact "UUID should be removed when the session is closed"
      (let [session (sub-system test/system)
            websocket-clients (:websocket-clients session)
            client {:client true}
            cookies {"sid" {:value (uuid/make-random-string)}}]
          (open-session! session cookies client)
          (count @websocket-clients) => 1
          (close-session! session client)
          (empty? @websocket-clients) => true))

(fact "Should be able to login")

(fact "Should be able to logout")