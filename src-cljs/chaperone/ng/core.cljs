(ns ^{:doc "The angularJS core implementation for the front end of this site"}
	chaperone.ng.core
	(:use-macros
		[purnam.js :only [obj]]
		[purnam.angular :only [def.module def.config]]))

(def.module chaperone.app [ngRoute])

;; configure routes
(def.config chaperone.app [$routeProvider]
			(doto $routeProvider
				(.when "/admin/users/list" (obj :templateUrl "/public/partials/admin/user/list.html"))
				(.otherwise (obj :templateUrl "/public/partials/index.html"))))