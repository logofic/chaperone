(ns ^{:doc "User accounts and login management"}
    chaperone.ng.account
    (:require chaperone.ng.core
              [chaperone.session :as session]
              [chaperone.messagebox :as mb])
    (:use [purnam.native :only [aget-in aset-in]]
          [cljs.core.async :only [<!]])
    (:use-macros
        [chaperone.ng.core :only [ng-apply]]
        [purnam.core :only [obj ! ? f.n]]
        [purnam.angular :only [def.controller]]
        [cljs.core.async.macros :only [go]]))

(defn- set-current-user!
    [$scope current-user]
    (ng-apply $scope
              (! $scope.loggedIn (-> current-user nil? not))
              (when current-user
                  (! $scope.clj-current-user current-user)
                  (! $scope.currentUser (clj->js current-user)))))

(def.controller chaperone.app.AccountCtrl [$scope System]
                (! $scope.init
                   (fn []
                       (let [chan (session/current-user System)]
                           (go (set-current-user! $scope (:data (<! chan)))))))

                (! $scope.login
                   (fn []
                       (go (let [response (<! (session/login System $scope.email $scope.password))
                                 messagebox (mb/sub-system System)
                                 current-user (:data response)]
                               (if current-user
                                   (set-current-user! $scope current-user)
                                   (mb/send-message! messagebox :danger "Your username or password is incorrect. Please try again."))))))

                (! $scope.logout
                   (fn []
                       (go (let [response (<! (session/logout System))]
                               (ng-apply
                                   (! $scope.loggedIn false)
                                   (! $scope.clj-current-user nil)
                                   (! $scope.currentUser nil)))))))