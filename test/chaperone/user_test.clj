(ns chaperone.user-test
    (:use [midje.sweet]
          [chaperone.user]
          [chaperone.crossover.user]
          [chaperone.crossover.rpc :only [new-request]])
    (:require [test-helper :as test]
              [chaperone.persistence.install :as install]
              [clj-time.core :as time]
              [clojurewerkz.elastisch.rest.index :as esi]
              [clojurewerkz.elastisch.rest.document :as esd]
              [clojurewerkz.elastisch.query :as esq]
              [chaperone.persistence.install :as install]
              [chaperone.persistence.core :as pcore]
              [chaperone.web.rpc :as rpc]
              [clojure.edn :as edn]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create)
    (test/start pcore/start!)
    (esi/delete @test/es-index)
    (install/create-index test/system))

(namespace-state-changes (before :facts (setup!)))

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

(fact "Saving a new user should encrypt the password."
      (let [test-user (new-user "Mark" "Mandel" "email" "password")
            persistence (pcore/sub-system test/system)]
          (:password test-user) => "password"
          (esd/delete-by-query @test/es-index "user" (esq/match-all))
          (save-user persistence test-user)
          (pcore/refresh persistence)
          (let [reget-user (get-user-by-id persistence (:id test-user))]
              test-user => (contains (dissoc reget-user :password))
              (:password test-user) =not=> (:password reget-user))))

(fact "Updating a user should not re-encrypt the password."
      (let [test-user (new-user "Mark" "Mandel" "email" "password")
            persistence (pcore/sub-system test/system)]
          (:password test-user) => "password"
          (esd/delete-by-query @test/es-index "user" (esq/match-all))
          (save-user persistence test-user)
          (pcore/refresh persistence)
          (let [reget-user (get-user-by-id persistence (:id test-user))]
              (:password test-user) =not=> (:password reget-user)
              (save-user persistence reget-user)
              (pcore/refresh persistence)
              (let [reget-user2 (get-user-by-id persistence (:id reget-user))]
                  reget-user => reget-user2))))

(fact "You should be able to get a user by email"
      (let [test-user (new-user "Mark" "Mandel" "email@email.com" "unique password")
            persistence (pcore/sub-system test/system)]
          (esd/delete-by-query @test/es-index "user" (esq/match-all))
          (save-user persistence test-user)
          (pcore/refresh persistence)
          (let [test-user (get-user-by-id persistence (:id test-user))
                reget-user (get-user-by-email persistence (:email test-user))]
              test-user => reget-user)))

(fact "You should be able to verify the password" :focus
      (let [test-user (new-user "Mark" "Mandel" "email@email.com" "my password of doom")
            persistence (pcore/sub-system test/system)]
          (esd/delete-by-query @test/es-index "user" (esq/match-all))
          (save-user persistence test-user)
          (pcore/refresh persistence)
          (let [test-user (get-user-by-id persistence (:id test-user))                ]
              (verify-user-password test-user "my password of doom") => true
              (verify-user-password test-user "INCORRECT") => false
              )))

(fact
    "Test if the _source->User works properly"
    (esi/delete @test/es-index)
    (install/create-index test/system)
    (let [test-user (new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now) :photo "photo.jpg")
          persistence (pcore/sub-system test/system)]
        (pcore/save persistence test-user)
        (let [_source->User (partial _source->User persistence)
              result (get-user-by-id persistence (:id test-user))]
            (doseq [key (keys result)]
                (key test-user) => (key result))
            )
        ))

(fact "Be able to list all users"
      (let [test-user1 (new-user "Mark" "Mandel" "email" "password")
            test-user2 (new-user "ZAardvark" "ZAbigail" "email" "password")
            persistence (pcore/sub-system test/system)]
          (esi/delete @test/es-index)
          (install/create-index test/system)
          (doto persistence
              (pcore/save test-user1)
              (pcore/save test-user2))
          (esi/refresh @test/es-index)
          (list-users persistence) => [test-user1 test-user2]))

(fact "RPC: Be able to list all users"
      (let [test-user1 (new-user "Mark" "Mandel" "email" "password")
            test-user2 (new-user "ZAardvark" "ZAbigail" "email" "password")
            persistence (pcore/sub-system test/system)
            request (new-request :user :list {})]
          (esi/delete @test/es-index)
          (install/create-index test/system)
          (doto persistence
              (pcore/save test-user1)
              (pcore/save test-user2))
          (pcore/refresh persistence)
          (rpc/run-rpc-request test/system request) => [test-user1 test-user2]))

(fact "RPC: Save user should store a user"
      (esi/delete @test/es-index)
      (install/create-index test/system)
      (let [test-user (new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now) :photo "photo.jpg")
            request (new-request :user :save test-user)
            persistence (pcore/sub-system test/system)]
          (rpc/run-rpc-request test/system request)
          (pcore/refresh persistence)
          (let [result-user (pcore/get-by-id persistence "user" (:id test-user))]
              (:id test-user) => (:id (_source->User persistence (:_source result-user))))))

(fact "EDN conversion should be able to happen"
      (let [test-user1 (new-user "Mark" "Mandel" "email" "password")
            rpc (rpc/sub-system test/system)
            edn (pr-str test-user1)]
          (edn/read-string {:readers (:edn-readers rpc)} edn) => test-user1
          ))

(fact "RPC: Save user should store a user"
      (esi/delete @test/es-index)
      (install/create-index test/system)
      (let [test-user (new-user "Mark" "Mandel" "email" "password" :last-logged-in (time/now) :photo "photo.jpg")
            persistence (pcore/sub-system test/system)]
          (save-user persistence test-user)
          (pcore/refresh persistence)
          (let [test-user (get-user-by-id persistence (:id test-user))
                request (new-request :user :load (:id test-user))]
              (rpc/run-rpc-request test/system request) => test-user)))
