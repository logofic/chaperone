(ns ^{:doc "Controller for the messagebox display"}
    chaperone.ng.messagebox
    ;use to specify the order things need to run in.
    (:require chaperone.ng.core
              [chaperone.messagebox :as mb]
              purnam.types.clojure)
    (:use [purnam.native :only [aget-in aset-in js-equals]]
          [cljs.core.async :only [<!]])
    (:use-macros
        [chaperone.ng.core :only [ng-apply]]
        [purnam.core :only [obj ! ? f.n]]
        [purnam.angular :only [def.controller]]
        [cljs.core.async.macros :only [go]]
        [while-let.core :only [while-let]]))

(defn- add-message!
    [$scope $timeout message]
    (let [js-message (clj->js message)]
        (ng-apply $scope
                  (! $scope.messages (-> js-message (cons $scope.messages) clj->js)))
        ($timeout (f.n []
                       (! $scope.messages
                          ;; use angular.equals to dodge the angular added properties.
                          (-> (remove #(js/angular.equals js-message %) $scope.messages) clj->js))
                       ) 4000)))

(defn- start-message-queue-listening!
    "Start listening to the message queue"
    [system $scope $timeout]
    (let [mb (mb/sub-system system)
          queue (:message-queue mb)]
        (go (while-let [message (<! queue)]
                       (add-message! $scope $timeout message)))))

(def.controller chaperone.app.MessageBoxCtrl [$scope $timeout System]
                (! $scope.init
                   (fn []
                       (! $scope.messages (array))
                       (start-message-queue-listening! System $scope $timeout)))

                (! $scope.messageClass
                   (fn [level]
                       (str "alert-" level))))