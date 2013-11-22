(ns ^{:doc "Tests for the user controller"}
    chaperone.ng.admin.user-test
    (:require [chaperone.ng.admin.user :as admin-user])
    (:use [purnam.cljs :only [aset-in aget-in]])
    (:use-macros
        [purnam.js :only [obj arr !]]
        [purnam.test :only [init describe it is is-not]]
        [purnam.test.angular :only [describe.ng describe.controller]]))

(init)

(describe.controller {:doc        "Testing AdminUserCtrl"
                      :module     chaperone.app
                      :controller AdminUserCtrl}

                     (.log js/console "Scope: " $scope) ;; coming back as null

                     (it "Should have a title in the scope"
                         (do ($scope.init)
                             (is $scope.title "Add"))))
