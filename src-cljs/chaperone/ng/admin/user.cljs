(ns ^{:doc "The angularJS core implementation for the front end of this site"}
    chaperone.ng.admin.user
    ;use to specify the order things need to run in.
    (:require [chaperone.ng.core :as core]
              [chaperone.crossover.user :as x-user]
              [chaperone.user :as user]
              [cljs.core.async :refer [>! <!]])
    (:use [purnam.cljs :only [aget-in aset-in]])
    (:use-macros
        [purnam.js :only [obj ! !>]]
        [purnam.angular :only [def.controller]]
        [cljs.core.async.macros :only [go]]))

(def.controller chaperone.app.AdminUserCtrl [$scope $location]
                (! $scope.init
                   (fn []
                       (! $scope.title "Add")))
                (! $scope.load-user
                   (fn []
                       (let [user (x-user/new-user "" "" "" "")]
                           (! $scope.user (clj->js user)))))
                (! $scope.save-user
                   (fn []
                       (let [user (x-user/map->User (js->clj $scope.user))
                             chan (user/save-user user)]
                           (go (let [result (<! chan)]
                                   (! $scope.alert (obj :category "success" :message "User has been saved successfully"))
                                   (!> $location.path "/admin/users/list")))))))

