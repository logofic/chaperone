(ns ^{:doc "Tests for the websocket system"}
    chaperone.websocket-test
    (:require [chaperone.core :as core])
    (:use [purnam.native :only [aset-in aget-in]]
          [chaperone.websocket :only [create-system sub-system send!]]
          [chaperone.crossover.rpc :only [new-request new-response]]
          [cljs.core.async :only [take!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.async :only [runs waits-for]]))

(describe {:doc "Websocket subsystem"}
          (it "should have the websocket subsystem"
              (let [system (core/create-system "localhost" 8080)]
                  (is (map? (sub-system system)) true))))

(describe {:doc     "Websocket RPC"
           :globals [system (core/create-system "localhost" 8080)
                     ws-system (sub-system system)
                     ws-chan (:chan ws-system)
                     rpc-map (:rpc-map ws-system)]}
          (it "It should take a RPC request, put it in the websocket channel, and setup the response channel handler"
              (let [request (new-request "Category" "Action" {:key "value"})
                    result (atom false)]
                  (take! ws-chan #(reset! result %))
                  (runs (send! ws-system request))
                  (waits-for "No value placed in Websocket channel" 1000 @result)
                  (runs (is (contains? @rpc-map (:id @result)) true)))))