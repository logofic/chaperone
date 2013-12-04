(ns ^{:doc "The web socket layer for this application"}
    chaperone.websocket
    (:use
        [cljs.core.async :only [chan >! <!]]
        [chaperone.crossover.rpc :only [new-request new-response]])
    (:use-macros
        [purnam.core :only [obj !]]
        [cljs.core.async.macros :only [go]]))


;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system host port]
    (let [sub-system {:host host
                      :port port
                      :chan (chan)}]
        (assoc system :websocket sub-system))
    )

(defn sub-system
    "get the persistence system from the global"
    [system]
    (:websocket system))
