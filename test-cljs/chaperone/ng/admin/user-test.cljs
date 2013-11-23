(ns chaperone.ng.admin.user-test
    (:require [chaperone.ng.admin.user :as admin-user])
    (:use [purnam.cljs :only [aset-in aget-in]])
    (:use-macros
        [purnam.js :only [obj arr !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.ng describe.controller
                                    it-uses it-compiles]]))

(init)

(describe.controller {:doc        "Testing AdminUserCtrl"
                      :module     chaperone.app
                      :controller AdminUserCtrl}

                     (it "Should have a title in the scope"
                         (.log js/console "Scope: " $scope) ;; coming back as null
                         ($scope.init)
                         (is $scope.title "Add")))