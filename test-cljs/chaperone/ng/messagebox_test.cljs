(ns ^{:doc "tests for the messagebox controller"}
    chaperone.ng.messagebox-test
    (:require chaperone.ng.messagebox
              purnam.types.clojure
              [chaperone.messagebox :as mb])
    (:use [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]]
          [cljs.core.async :only [take!]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]
        [purnam.test.angular :only [describe.controller describe.ng]]
        [purnam.test.async :only [runs waits-for]]))

(init-tests)

(describe.ng
    {:doc    "MessageBoxCtrl"
     :module chaperone.app
     :inject [[$scope ([$rootScope $controller]
                       ($controller "MessageBoxCtrl" (obj :$scope ($rootScope.$new))))]
              System
              $timeout]}

    (it "Should have an empty array in scope on init"
        ($scope.init)
        (is $scope.messages (array)))

    (it "should put the message in the array when it is added"
        ($scope.init)
        (runs
            (is $scope.messages (array))
            (mb/send-message! (mb/sub-system System) :info "Hello World!"))

        (waits-for "Messages doesn't get the message" 1000 (not (empty? $scope.messages)))
        (runs
            (is (first $scope.messages) (obj :level "info" :message "Hello World!"))
            (mb/send-message! (mb/sub-system System) :warning "Hello World!")
            )
        (waits-for "Messages should have a second message" 1000 (= (count $scope.messages) 2))
        (runs
            (is (first $scope.messages) (obj :level "warning" :message "Hello World!"))))

    (it "should remove a message from the array after 4 seconds"
        ($scope.init)
        (runs
            (is $scope.messages (array))
            (mb/send-message! (mb/sub-system System) :info "Hello World!"))
        (waits-for "Messages doesn't get the message" 1000 (not (empty? $scope.messages)))
        (runs
            ($timeout.flush)
            (is (empty? $scope.messages) true))
        )

    )