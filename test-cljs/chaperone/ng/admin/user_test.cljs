(ns chaperone.ng.admin.user_test
    (:require [chaperone.ng.admin.user :as admin-user]
              [chaperone.user :as user])
    (:use [purnam.native :only [aset-in aget-in]]
          [cljs.core.async :only [chan put!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.controller describe.ng]]
        [purnam.test.async :only [runs waits-for]]))

(init)

(describe.ng
    {:doc    "Testing AdminUserCtrl"
     :module chaperone.app
     :inject [[$scope ([$rootScope $controller $location]
                       ($controller "AdminUserCtrl" (obj :$scope ($rootScope.$new) :$location $location)))]
              [$location ([$location] $location)]]}

    (it "Should have a title in the scope"
        ($scope.init)
        (is $scope.title "Add"))

    (it "Should create a new user into scope, when a non existent usersid is used"
        ($scope.load-user)
        (is $scope.user.firstname "")
        (is $scope.user.lastname "")
        (is $scope.user.email "")
        (is $scope.user.password ""))

    (it "Should show a message and change the location when a user is saved"
        ($scope.load-user)
        (! $scope.user.firstname "John")
        (! $scope.user.lastname "Doe")
        (! $scope.user.email "email@email.com")
        (! $scope.user.password "password")
        (let [ws-chan (chan)]
            (with-redefs [user/save-user (fn [user] ws-chan)]
                         ($scope.save-user)
                         (runs (put! ws-chan {}))
                         (waits-for "Location never gets set", 1000 (= ($location.path) "/admin/users/list"))
                         (runs (is $scope.alert.category "success"))))))