(defproject chaperone "0.1.0-SNAPSHOT"
	:description "Collaborative work environment with Chat, Audio and Video conferencing over WebRTC"
	:url "http://www.github.com/markmandel/chaperone"
	:license {:name "Apache License, Version 2.0"
						:url "http://www.apache.org/licenses/LICENSE-2.0.html"}
	:dependencies [[org.clojure/clojure "1.5.1"]
								 [clj-time "0.6.0"]
								 [codesignals.flux "0.1.2"]
								 [cljs-uuid "0.0.4"]
								 [environ "0.4.0"]
								 [clojurewerkz/elastisch "1.3.0-SNAPSHOT"]
								 ]
	:main chaperone.core
	:profiles {:uberjar {:aot :all}
						 :dev {:dependencies [[midje "1.5.1"]]
									 :env {:elasticsearch-url "http://dev.chaperone:9200"
												 :elaticsearch-index "test_chaperone"}}}
	:plugins [[lein-midje "3.0.0"]
						[codox "0.6.4"]
						[lein-environ "0.4.0"]])
