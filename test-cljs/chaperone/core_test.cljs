(ns chaperone.core-test
    (:use [test-helper :only [init-tests]]
          [purnam.native :only [aset-in aget-in]]
          [chaperone.core :only [create-system]])
    (:use-macros
        [purnam.core :only [obj !]]
        [purnam.test :only [init describe it is]]))

(init-tests)

(describe {:doc "Create the system"}
          (it "Should return a map"
              (is true (map? (create-system "localhost" 80)))))