(ns ^{:doc "Core functionality for persistance"}
    chaperone.persistence.core
    (:require [environ.core :as env]
              [cheshire.generate :as chesg]
              [clj-time.format :as timef]
              [clojurewerkz.elastisch.rest :as esr]
              [clojurewerkz.elastisch.rest.index :as esi]
              [clojurewerkz.elastisch.rest.document :as esd]))

;;; system
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:elasticsearch-url   (get env/env :elasticsearch-url "http://localhost:9200")
                      :elasticsearch-index (get env/env :elasticsearch-index "chaperone")
                      :date-formatter      (timef/formatters :date-time)}]
        (assoc system :persistence sub-system))
    )

(defn sub-system
    "get the persistence system from the global"
    [system]
    (:persistence system))

(defn get-es-index
    "Returns the ES index we are using from the system"
    [system]
    (-> system sub-system :elasticsearch-index))

(defn- date-formatter
    "get the date formatter from a system object"
    [system]
    (-> system sub-system :date-formatter))

(defn start!
    "Start the persistence mechanism"
    [system]
    (esr/connect! (-> system sub-system :elasticsearch-url))

    ;;; extend json handler for joda dates - yyyy-MM-dd’T’HH:mm:ss.SSSZZ (es date_time format)
    (chesg/add-encoder org.joda.time.DateTime
                       (fn [c jsonGenerator]
                           (.writeString jsonGenerator (timef/unparse (date-formatter system) c))))
    system)

;;; logic

(defmulti get-type "Returns the es type of this persistence record" class)

(defn parse-string-date
    "Parse the standard date format for persistence"
    [persistence date]
    (if date (timef/parse (:date-formatter persistence) date)))

(defn save
    "utility class for easy inserting of a Persistent record"
    [persistence record]
    (esd/put (:elasticsearch-index persistence) (get-type record) (:id record) record))

(defn get-by-id
    "Get a specific type by id"
    [persistence type id]
    (esd/get (:elasticsearch-index persistence) type id))

(defn refresh
    "Refresh the the chaperone elastic search index"
    [persistence]
    (esi/refresh (:elasticsearch-index persistence)))

(defn- search-with-options
    "Utilitiy function for passing through to esd/search with the required option map"
    [persistence mapping-type options]
    (apply esd/search (:elasticsearch-index persistence) mapping-type (mapcat identity options)))

(defn search
    "Search the mapping-type, with the given properties"
    [persistence mapping-type & {:as options}]
    (search-with-options persistence mapping-type options))

(defn search-to-record
    "Search the mapping-type, and convert it to defrecords using a transformer function"
    [persistence mapping-type transformer & {:as options}]
    (let [results (-> (search-with-options persistence mapping-type options) :hits :hits)]
        (map (fn [item] (-> item :_source transformer)) results)))