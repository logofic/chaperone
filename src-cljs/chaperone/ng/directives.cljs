(ns ^{:doc "Directives for this app. May be split into own namespaces later"}
    chaperone.ng.directive
    (:require chaperone.ng.core
              )
    (:use [purnam.native :only [aget-in aset-in]]
          [jayq.core :only [$ attr find]])
    (:use-macros
        [purnam.core :only [f.n obj !]]
        [purnam.angular :only [def.directive]]))

;; Clickable rows on a table
(def.directive chaperone.app.clickRow
               [$location]
               (obj :link (f.n [scope elem attr]
                               (let [a (find ($ elem) "a")
                                     href (attr a "href")]
                                   (js/console.log "row! v7" href))
                               )))