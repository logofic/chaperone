(ns ^{:doc "Tests for directives"}
    chaperone.ng.directive-test
    (:require [jayq.core :as j]
              chaperone.ng.directive)
    (:use [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.ng]]))

(describe.ng
    {:doc    "Click Row Directive"
     :module chaperone.app
     :inject [$compile $rootScope $location]}

    (it "when using row-click directive, the path should be the href location when the row is clicked"
        (let [table (($compile "<table><tbody><tr click-row><td><a href='/link'>link</a></td></tr></tbody></table>") $rootScope)]
            (-> table j/$ (j/find :tr) (j/trigger :click))
            ($location.path) => "/link")))