(ns ^{:doc "Manage the web facing part of the application"}
	chaperone.web.core
	(require [environ.core :as env]))

;;; system tools
(defn create-sub-system
	"Create the persistence system. Takes the existing system details"
	[system]
	(let [sub-system {:port (env/env :web-server-port 8080)}]
		(assoc system :web sub-system)))

;;; logic
