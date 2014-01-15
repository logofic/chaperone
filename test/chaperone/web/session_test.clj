(ns ^{:doc "Management of the application web sessions"}
    chaperone.web.session-test
    (:use [midje.sweet]
          [chaperone.web.session])
    (:require [test-helper :as test]
              [cljs-uuid.core :as uuid]
              [clojurewerkz.elastisch.rest.document :as esd]
              [clojurewerkz.elastisch.query :as esq]
              [chaperone.user :as user]
              [chaperone.crossover.user :as x-user]
              [chaperone.persistence.core :as pcore]))

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
          (get @websocket-clients client) => (get-in cookies ["sid" :value])
          (get-client-sid session client) => (get-in cookies ["sid" :value])))

(fact "UUID should be removed when the session is closed"
      (let [session (sub-system test/system)
            websocket-clients (:websocket-clients session)
            client {:client true}
            cookies {"sid" {:value (uuid/make-random-string)}}]
          (open-session! session cookies client)
          (count @websocket-clients) => 1
          (close-session! session client)
          (empty? @websocket-clients) => true))

(fact "Should be able to login"
      (esd/delete-by-query @test/es-index "user" (esq/match-all))
      (let [persistence (pcore/sub-system test/system)
            session (sub-system test/system)
            test-user (x-user/new-user "Mark" "Mandel" "email" "password")
            sid (uuid/make-random-string)
            cookies {"sid" {:value sid}}
            client {:client true}]
          (user/save-user persistence test-user)
          (pcore/refresh persistence)
          (open-session! session cookies client)
          (get-user-session session sid) => nil
          (login! test/system sid "email" "password") => truthy
          (login! test/system sid "email" "password") => (user/get-user-by-id persistence (:id test-user))
          (get-user-session session sid) => (user/get-user-by-id persistence (:id test-user))))

(fact "Should be able to logout"
      (esd/delete-by-query @test/es-index "user" (esq/match-all))
      (let [persistence (pcore/sub-system test/system)
            session (sub-system test/system)
            test-user (x-user/new-user "Mark" "Mandel" "email" "password")
            sid (uuid/make-random-string)
            cookies {"sid" {:value sid}}
            client {:client true}]
          (user/save-user persistence test-user)
          (pcore/refresh persistence)
          (open-session! session cookies client)
          (login! test/system sid "email" "password") => truthy
          (logout! session sid)
          (get-user-session session sid) => nil))