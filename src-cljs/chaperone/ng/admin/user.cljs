(ns ^{:doc "The angularJS core implementation for the front end of this site"}
	chaperone.ng.admin.user
	(:use [purnam.cljs :only [aget-in aset-in]]
		  ;use to specify the order things need to run in.
		  [chaperone.ng.core :only []])
	(:use-macros
		[purnam.js :only [obj !]]
		[purnam.angular :only [def.module def.controller]]))

(def.controller chaperone.app.AdminUserCtrl [$scope]
                (.log js/console "I have started!")

				(! $scope.init
				   (fn []
					   (! $scope.title "Add"))))