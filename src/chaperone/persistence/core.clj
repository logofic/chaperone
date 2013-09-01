(ns ^{:doc "Core functionality for persistance"}
	chaperone.persistence.core
	(:require [environ.core :as env]
						[cheshire.generate :as chesg]
						[clj-time.format :as timef]
						[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.index :as esi]
						[clojurewerkz.elastisch.rest.document :as esd]))

;;; Protocols

(defprotocol Persistent
	"Protocol for encapsulationg common persistence functions"
	(get-type [this] "Returns the es type of this persistence record")
	)

;;; extend json handler for joda dates - yyyy-MM-dd’T’HH:mm:ss.SSSZZ (es date_time format)
(def date-formatter (timef/formatters :date-time))

(chesg/add-encoder org.joda.time.DateTime
	(fn [c jsonGenerator]
		(.writeString jsonGenerator (timef/unparse date-formatter c))))

;;; set default connection to elastic search
(esr/connect! (env/env :elasticsearch-url))

(def es-index
	"The index that we store the data against in elastic search"
	(env/env :elaticsearch-index))

(defn create [^chaperone.persistence.core.Persistent record]
	"utility class for easy inserting of a Persistent record"
	(esd/create es-index (get-type record) record :id (:id record)))

(defn get-by-id [type id]
	(esd/get es-index type id))