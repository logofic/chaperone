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

(def.controller chaperone.app.AdminUserCtrl [$scope $location $routeParams System]
                (! $scope.initAddUserForm
                   (fn []
                       (let [user (x-user/new-user "" "" "" "")]
                           (! $scope.user (clj->js user)))))

                (! $scope.initEditUserForm
                   (fn []
                       (let [chan (user/get-user-by-id System $routeParams.id)]
                           (go (let [user (<! chan)]
                                   (ng-apply $scope (! $scope.user (clj->js user))))))))

                (! $scope.saveUser
                   (fn []
                       (let [user (x-user/map->User (js->clj $scope.user :keywordize-keys true))
                             chan (user/save-user System user)]
                           (go (let [result (<! chan)]
                                   (ng-apply $scope
                                             ($location.path "/admin/users/list")))))))
                (! $scope.initListUsers
                   (fn []
                       (let [chan (user/list-users System)]
                           (go (let [result (<! chan)]
                                   (ng-apply $scope (! $scope.userList (-> result :data clj->js)))))))))


