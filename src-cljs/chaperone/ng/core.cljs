(ns ^{:doc "The angularJS core implementation for the front end of this site"}
    chaperone.ng.core
    (:require [chaperone.core :as core])
    (:use [purnam.native :only [aget-in aset-in]])
    (:use-macros
        [purnam.core :only [obj ! !>]]
        [purnam.angular :only [def.module def.config def.factory]]))

(def.module chaperone.app [ngRoute])

(def.factory chaperone.app.System [$location]
             (.log js/console "I've created a monster!")
             (-> (core/create-system (!> $location.host) (!> $location.path)) core/start))

;; configure routes
(def.config chaperone.app [$routeProvider]
            (doto $routeProvider
                (.when "/admin/users/add" (obj :templateUrl "/public/partials/admin/user/user-form.html" :controller "AdminUserCtrl"))
                (.when "/admin/users/list" (obj :templateUrl "/public/partials/admin/user/list.html" :controller "AdminUserCtrl"))
                (.otherwise (obj :templateUrl "/public/partials/index.html"))))


