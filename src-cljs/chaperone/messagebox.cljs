(ns ^{:doc "Messagebox display system"}
    chaperone.messagebox
    (:use [cljs.core.async :only [chan put!]]))

(defn create-sub-system
    "Creates the messagebox subsystem"
    [system]
    (let [sub-system {:message-queue (chan)}]
        (assoc system :messagebox sub-system)))

(defn sub-system
    "Get the messagebox sub system"
    [system]
    (:messagebox system))

(defn send-message!
    "Queues up a message box request. levels are: :success, :info, :warning and :danger"
    [messagebox level message]
    (let [message-queue (:message-queue messagebox)]
        (put! message-queue {:level level :message message})))