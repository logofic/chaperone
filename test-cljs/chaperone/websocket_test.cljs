(ns ^{:doc "Tests for the websocket system"}
    chaperone.websocket-test
    (:require [chaperone.core :as core])
    (:use [purnam.native :only [aset-in aget-in]]
          [chaperone.websocket :only [create-system sub-system]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]))

(describe {:doc "Websocket subsystem"}
          (it "should have the websocket subsystem"
              (let [system (core/create-system "localhost" 8080)]
                  (is (map? (sub-system system)) true))))