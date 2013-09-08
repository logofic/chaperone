(ns user
	"Tools for interactive development with the REPL. This file should
	not be included in a production build of the application."
	(:use [midje.repl :only (autotest load-facts)]))