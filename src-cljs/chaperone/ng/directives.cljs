(ns ^{:doc "Directives for this app. May be split into own namespaces later"}
    chaperone.ng.directive
    (:require chaperone.ng.core
              [jayq.core :as j])
    (:use [purnam.native :only [aget-in aset-in]]
          [clojure.string :only [replace-first]])
    (:use-macros
        [purnam.core :only [f.n obj !]]
        [purnam.angular :only [def.directive]]
        [chaperone.ng.core :only [ng-apply]]))

;; Clickable rows on a table
(def.directive chaperone.app.clickRow
               [$location]
               (let [link (f.n [scope row attr]
                               (let [row (j/$ row)]
                                   (j/on row :click (f.n [e]
                                                         (let [href (-> row (j/find :a) (j/attr :href))]
                                                             (ng-apply scope ($location.path (replace-first href "#" ""))))))))]
                   (obj :link link)))