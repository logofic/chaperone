(ns ^{:doc "Tests for the user controller"}
    chaperone.ng.admin.user-test
    (:require [chaperone.ng.admin.user :as admin-user])
    (:use [purnam.cljs :only [aset-in aget-in]])
    (:use-macros
        [purnam.js :only [obj arr !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.ng describe.controller]]))

(init)

;; why am I not seeing tests?
(describe {:doc "will this run an actual test?"}
          (it "should list a test"
              (is "foo" "bar")))

(describe.controller {:doc        "Testing AdminUserCtrl"
                      :module     chaperone.app
                      :controller AdminUserCtrl}

                     (it "Should have a title in the scope"
                         (is $scope.title "Add")))