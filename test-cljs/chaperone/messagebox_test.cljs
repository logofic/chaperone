(ns chaperone.messagebox-test
    (:require [chaperone.core :as core])
    (:use [chaperone.messagebox :only [sub-system send-message!]]
          [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]]
          [cljs.core.async :only [take!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [describe it is]]
        [purnam.test.async :only [runs waits-for]]
        ))

(init-tests)

(describe {:doc "Message queuing"}
          (it "should put a message in the queue"
              (let [system (core/create-system "localhost" 80)
                    mb (sub-system system)
                    chan (:message-queue mb)
                    result (atom false)]
                  (runs
                      (js/console.log "WHAT?")
                      (take! chan #(reset! result %))
                      (send-message! mb :info "Hello World!")
                      )
                  (waits-for "No value in the message queue" 2000 @result)
                  (runs
                      (is @result {:level :info :message "Hello World!"})))))