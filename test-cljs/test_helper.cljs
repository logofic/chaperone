(ns ^{:doc "Tests for the websocket system"}
    test-helper
    (:require [chaperone.websocket :as ws])
    (:use [purnam.native :only [aset-in aget-in]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init]]))


(defn mock-websocket
    []
    "Setup the websocket mock"
    (! js/window.WebSocket (fn [url] (obj))))

(defn init-tests
    "Init for all tests"
    []
    (mock-websocket)
    (init))
