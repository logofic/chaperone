(ns ^{:doc "Tests for the account controller"}
    chaperone.ng.account-test
    (:require chaperone.ng.account
              [chaperone.session :as session]
              [chaperone.crossover.user :as x-user]
              [chaperone.crossover.rpc :as x-rpc])
    (:use [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]]
          [cljs.core.async :only [chan put!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.controller describe.ng]]
        [purnam.test.async :only [runs waits-for]]))

(init-tests)

(describe.controller
    {:doc        "Testing the account controller"
     :module     chaperone.app
     :controller AccountCtrl}
    (it "Should come back as logged in if a user exists"
        (let [user (x-user/new-user "M" "L" "E" "P")]
            (with-redefs [session/current-user (fn [system]
                                                   (let [chan (chan)
                                                         request (x-rpc/new-request :account :current {})
                                                         response (x-rpc/new-response request user)]
                                                       (put! chan response)
                                                       chan
                                                       ))]
                         ($scope.init)
                         (waits-for "Never set to being logged in" 1000 $scope.loggedIn)
                         (runs
                             (is $scope.clj-current-user user)
                             (is $scope.currentUser (clj->js user))
                             ))))

    (it "Should come be not logged in if the user doesn't exist."
        (let [user nil]
            (with-redefs [session/current-user (fn [system]
                                                   (let [chan (chan)
                                                         request (x-rpc/new-request :account :current {})
                                                         response (x-rpc/new-response request user)]
                                                       (put! chan response)
                                                       chan
                                                       ))]
                         ($scope.init)
                         (waits-for "Never set to being not logged" 1000 (not $scope.loggedIn))
                         (runs
                             (is $scope.clj-current-user user)
                             (is (type $scope.currentUser) nil)))))

    )