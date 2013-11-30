(ns chaperone.ng.admin.user_test.cljs
    (:require [chaperone.ng.admin.user :as admin-user])
    (:use [purnam.cljs :only [aset-in aget-in]])
    (:use-macros
        [purnam.js :only [obj]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.controller]]))

(init)

(describe.controller {:doc        "Testing AdminUserCtrl"
                      :module     chaperone.app
                      :controller AdminUserCtrl}

                     (it "Should have a title in the scope"
                         ($scope.init)
                         (is $scope.title "Add"))

                     (it "Should create a new user into scope, when a non existent usersid is used"
                         ($scope.load-user)
                         (is $scope.user.firstname "")
                         (is $scope.user.lastname "")
                         (is $scope.user.email "")
                         (is $scope.user.password "")))