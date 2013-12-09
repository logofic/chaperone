(ns ^{:doc "Tests for the websocket system"}
    chaperone.websocket-test
    (:require [chaperone.core :as core])
    (:use [purnam.native :only [aset-in aget-in]]
          [chaperone.websocket :only [create-system sub-system send! start! stop!]]
          [chaperone.crossover.rpc :only [new-request new-response]]
          [cljs.core.async :only [take! put!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.async :only [runs waits-for]]))

(describe {:doc "Websocket subsystem"}
          (it "should have the websocket subsystem"
              (let [system (core/create-system "localhost" 8080)]
                  (is (map? (sub-system system)) true))))

(describe {:doc     "Websocket RPC (Not started)"
           :globals [system (core/create-system "localhost" 8080)
                     ws-system (sub-system system)
                     ws-chan (:request-chan ws-system)
                     rpc-map (:rpc-map ws-system)]}
          (it "Should take a RPC request, put it in the websocket channel, and setup the response channel handler"
              (let [request (new-request :request-category :request-action {:key "value"})
                    result (atom false)]
                  (take! ws-chan #(reset! result %))
                  (runs (send! ws-system request))
                  (waits-for "No value placed in Websocket channel" 1000 @result)
                  (runs (is (contains? @rpc-map (:id @result)) true)))))

(describe {:doc     "Websocket RPC (Started)"
           :globals [system (core/create-system "localhost" 8080)
                     ws-system (sub-system system)
                     ws-chan (:request-chan ws-system)
                     rpc-map (:rpc-map ws-system)
                     response-chan (:response-chan ws-system)]}
          (start! system)
          (it "Should send back a response on the returned request channel, when a response is sent back"
              (let [request (new-request :thing :do-thing {:key "value"})
                    ws-complete (atom false)
                    response (new-response request {:data "oooer"})
                    response-result (atom false)]
                  ; don't do anything with it, we just need it because it's unbuffered.
                  (take! ws-chan (fn [v] (reset! ws-complete v)))
                  (runs (take! (send! ws-system request) (fn [v] (reset! response-result v))))
                  (waits-for "No value placed in Websocket channel" 1000 @ws-complete)
                  (runs (put! response-chan (prn-str response)))
                  (waits-for "No value returned on RPC's channel" 1000 @response-result)
                  (runs
                      (is (= (-> @response-result :request) request) true)
                      (is (= (:data @response-result) {:data "oooer"}) true))
                  )))