(ns chaperone.ng.core-test
    (:require [chaperone.ng.core :as ng-core])
    (:use [purnam.native :only [aset-in aget-in]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.ng]]))

(init)

(describe.ng {:doc "Test the System service factory"
              :module chaperone.app
              :inject [System]}
             (it "Should be a map"
                 (is (map? System) true))
             (it "Should have a websocket key"
                 (is (contains? System :websocket) true)))