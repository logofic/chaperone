(ns ^{:doc "The angularJS core implementation for the front end of this site"}
    chaperone.ng.admin.user
    ;use to specify the order things need to run in.
    (:require [chaperone.ng.core :as core])
    (:use [purnam.cljs :only [aget-in aset-in]])
    (:use-macros
        [purnam.js :only [obj !]]
        [purnam.angular :only [def.controller]]))

(def.controller chaperone.app.AdminUserCtrl [$scope]
                (! $scope.init
                   (fn []
                       (! $scope.title "Add"))))
