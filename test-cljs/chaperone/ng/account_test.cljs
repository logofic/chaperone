(ns ^{:doc "Tests for the account controller"}
    chaperone.ng.account-test
    (:require chaperone.ng.account)
    (:use [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]]
          [cljs.core.async :only [chan put!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.controller describe.ng]]
        [purnam.test.async :only [runs waits-for]]))



(describe.controller
    {:doc        "Testing the account controller"
     :module     chaperone.app
     :controller AccountCtrl}
    )