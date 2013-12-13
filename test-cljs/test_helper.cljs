(ns ^{:doc "Tests for the websocket system"}
    test-helper
    (:require [chaperone.websocket :as ws])
    (:use [purnam.native :only [aset-in aget-in]])
    (:use-macros
        [purnam.test :only [init]]))

(defn init-tests
    "Init for all tests"
    []
    (init))
