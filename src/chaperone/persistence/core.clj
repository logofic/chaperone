(ns ^{:doc "Core functionality for persistance"}
	chaperone.persistence.core
	(:require [environ.core :as env]
			  [cheshire.generate :as chesg]
			  [clj-time.format :as timef]
			  [cljs-uuid.core :as uuid]
			  [clojurewerkz.elastisch.rest :as esr]
			  [clojurewerkz.elastisch.rest.index :as esi]
			  [clojurewerkz.elastisch.rest.document :as esd]))

;;; system
(defn create-sub-system
	"Create the persistence system. Takes the existing system details"
	[system]
	(let [sub-system {:elasticsearch-url  (env/env :elasticsearch-url)
					  :elaticsearch-index (env/env :elaticsearch-index)
					  :date-formatter     (timef/formatters :date-time)}]
		(assoc system :persistence sub-system))
	)

(defn sub-system
	"get the persistence system from the global"
	[system]
	(:persistence system))

(defn get-es-index
	"Returns the ES index we are using from the system"
	[system]
	(-> system sub-system :elaticsearch-index))

(defn- date-formatter
	"get the date formatter from a system object"
	[system]
	(-> system sub-system :date-formatter))

(defn start
	"Start the persistence mechanism"
	[system]
	(esr/connect! (-> system sub-system :elasticsearch-url))

	;;; extend json handler for joda dates - yyyy-MM-dd’T’HH:mm:ss.SSSZZ (es date_time format)
	(chesg/add-encoder org.joda.time.DateTime
					   (fn [c jsonGenerator]
						   (.writeString jsonGenerator (timef/unparse (date-formatter system) c))))
	system)

;;; logic

(defprotocol Persistent
	"Protocol for encapsulationg common persistence functions"
	(get-type [this] "Returns the es type of this persistence record")
	)

(defn parse-string-date
	"Parse the standard date format for persistence"
	[persistence date]
	(if date (timef/parse (:date-formatter persistence) date)))

(def ^:private es-index
	 "The index that we store the data against in elastic search"
	 (env/env :elaticsearch-index))

(defn create-id
	"creates a uuid string"
	[]
	(-> (uuid/make-random) .toString))

(defn create
	"utility class for easy inserting of a Persistent record"
	[^chaperone.persistence.core.Persistent record]
	(esd/create es-index (get-type record) record :id (:id record)))

(defn get-by-id
	"Get a specific type by id"
	[type id]
	(esd/get es-index type id))

(defn- search-with-options
	   "Utilitiy function for passing through to esd/search with the required option map"
	   [mapping-type options]
	   (apply esd/search es-index mapping-type (mapcat identity options)))

(defn search
	"Search the mapping-type, with the given properties"
	[mapping-type & {:as options}]
	(search-with-options mapping-type options))

(defn search-to-record
	"Search the mapping-type, and convert it to defrecords using a transformer function"
	[mapping-type transformer & {:as options}]
	(let [results (-> (search-with-options mapping-type options) :hits :hits)]
		(map (fn [item] (-> item :_source transformer)) results))) ()