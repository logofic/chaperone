(ns ^{:doc "User accounts and login management"}
    chaperone.ng.account
    (:require chaperone.ng.core)
    (:use [purnam.native :only [aget-in aset-in js-equals]])
    (:use-macros
        [purnam.core :only [obj ! ? f.n]]
        [purnam.angular :only [def.controller]]))

(def.controller chaperone.app.AccountCtrl [$scope System]
                (! $scope.init
                   (fn []
                       )))