(defproject chaperone "0.1.0-SNAPSHOT"
			:description "Collaborative work environment with Chat, Audio and Video conferencing over WebRTC"
			:url "http://www.github.com/markmandel/chaperone"
			:license {:name "Apache License, Version 2.0"
					  :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
			:dependencies [[org.clojure/clojure "1.5.1"]
						   [clj-time "0.6.0"]
						   [cljs-uuid "0.0.4"]
						   [environ "0.4.0"]
						   [clojurewerkz/elastisch "1.3.0-beta3"]
						   [http-kit "2.1.10"]
						   [compojure "1.1.5"]
						   [selmer "0.4.2"]
						   [dieter "0.4.1"]]
			:main chaperone.core
			:plugins [[lein-midje "3.1.1"]
					  [codox "0.6.4"]
					  [lein-environ "0.4.0"]]
			:profiles {:uberjar {:aot :all}
					   :dev     {:dependencies [[midje "1.6-beta1"]
												[org.clojure/tools.namespace "0.2.4"]
												[org.clojure/tools.trace "0.7.6"]]
								 :source-paths ["dev"]
								 :repl-options {:init-ns user}
								 :env          {:elasticsearch-url   "http://dev.chaperone:9200"
												:elasticsearch-index "test_chaperone"
												:web-server-port     8080}}

					   ;; profile specifically for compiling cljs, to remove unneccessary dependencies, and since
					   ;; it will blow up with dieter as it looks for the v8 native
					   :cljs    {:dependencies [[org.clojure/clojurescript "0.0-1889"]
												[purnam "0.1.0-beta"]]
								 :exclusions   [dieter http-kit compojure selmer environ]
								 :plugins      [[lein-cljsbuild "0.3.3"]]
								 :cljsbuild    {
												   :builds [{
																; The path to the top-level ClojureScript source directory:
																:source-paths   ["src-cljs"]
																:notify-command ["notify-send"]
																; The standard ClojureScript compiler options:
																; (See the ClojureScript compiler documentation for details.)
																:compiler       {:output-to     "resources/public/js/main.js"
																				 :optimizations :whitespace
																				 :pretty-print  true
																				 ;:source-map "resources/public/main.js.map"
																				 }}]}}})
