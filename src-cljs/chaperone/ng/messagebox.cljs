(ns ^{:doc "Controller for the messagebox display"}
    chaperone.ng.messagebox
    ;use to specify the order things need to run in.
    (:require chaperone.ng.core
              [chaperone.messagebox :as mb]
              purnam.types.clojure)
    (:use [purnam.native :only [aget-in aset-in]]
          [cljs.core.async :only [<!]])
    (:use-macros
        [chaperone.ng.core :only [ng-apply]]
        [purnam.core :only [obj ! ?]]
        [purnam.angular :only [def.controller]]
        [cljs.core.async.macros :only [go]]
        [while-let.core :only [while-let]]))

(defn- start-message-queue-listening!
    "Start listening to the message queue"
    [system $scope]
    (let [mb (mb/sub-system system)
          queue (:message-queue mb)]
        (go (while-let [message (<! queue)]
                       (ng-apply
                           (! $scope.messages (-> (clj->js message) (cons $scope.messages) clj->js)))))))

(def.controller chaperone.app.MessageBoxCtrl [$scope $timeout System]
                (! $scope.init
                   (fn []
                       (! $scope.messages (array))
                       (start-message-queue-listening! System $scope))))