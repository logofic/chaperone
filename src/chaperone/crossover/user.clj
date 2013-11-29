(ns ^{:doc "User state."}
    chaperone.crossover.user)

;;; User record
(defrecord User [id firstname lastname password email photo last-logged-in])