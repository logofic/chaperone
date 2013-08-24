(defproject chaperone "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
								 [clj-time "0.6.0"]
								 [codesignals.flux "0.1.2"]
								 [cljs-uuid "0.0.4"]]
  :main chaperone.core
  :profiles {:uberjar {:aot :all}
						 :dev {:dependencies [[midje "1.5.1"]]}}
	:plugins [[lein-midje "3.0.0"]
						[codox "0.6.4"]]
)
