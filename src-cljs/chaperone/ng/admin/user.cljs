(ns ^{:doc "The angularJS core implementation for the front end of this site"}
    chaperone.ng.admin.user
    ;use to specify the order things need to run in.
    (:require [chaperone.ng.core :as core]
              [chaperone.crossover.user :as x-user]
              [chaperone.user :as user]
              [cljs.core.async :refer [>! <!]])
    (:use [purnam.native :only [aget-in aset-in]])
    (:use-macros
        [chaperone.ng.core :only [ng-apply]]
        [purnam.core :only [obj !]]
        [purnam.angular :only [def.controller]]
        [cljs.core.async.macros :only [go]]))

(defn load-user
    "load up a user into the scope"
    [system $scope]
    (let [user (x-user/new-user "" "" "" "")]
        (! $scope.user (clj->js user))))

(def.controller chaperone.app.AdminUserCtrl [$scope $location System]
                (! $scope.init
                   (fn []
                       (! $scope.title "Add")))
                (load-user System $scope)
                (! $scope.saveUser
                   (fn []
                       (let [user (x-user/map->User (js->clj $scope.user))
                             chan (user/save-user System user)]
                           (go (let [result (<! chan)]
                                   (ng-apply $scope
                                             ($location.path "/admin/users/list"))))))))


