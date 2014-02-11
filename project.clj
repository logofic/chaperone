(defproject chaperone "0.1.0-SNAPSHOT"
    :description "Collaborative work environment with Chat, Audio and Video conferencing over WebRTC"
    :url "http://www.github.com/markmandel/chaperone"
    :license {:name "Apache License, Version 2.0"
              :url  "http://www.apache.org/licenses/LICENSE-2.0.html"}
    :dependencies [[org.clojure/clojure "1.5.1"]
                   [clj-time "0.6.0"]
                   [cljs-uuid "0.0.5"]
                   [environ "0.4.0"]
                   [clojurewerkz/elastisch "1.4.0"]
                   [http-kit "2.1.16"]
                   [selmer "0.5.9"]
                   [compojure "1.1.5"]
                   [dieter "0.4.1"]
                   [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                   [com.google.guava/guava "16.0.1"]
                   [clojurewerkz/scrypt "1.1.0"]
                   [while-let "0.1.0"]]
    :main chaperone.core
    :plugins [[lein-midje "3.1.1"]
              [codox "0.6.4"]
              [lein-environ "0.4.0"]
              [lein-ancient "0.5.2"]]
    :codox {:output-dir "doc/clj"}
    :profiles {:uberjar {:aot :all}
               :dev     {:dependencies [[midje "1.6.0"]
                                        [org.clojure/tools.namespace "0.2.4"]
                                        [org.clojure/tools.trace "0.7.6"]
                                        [org.clojars.gjahad/debug-repl "0.3.3"]
                                        [clj-webdriver "0.6.1"]]
                         :source-paths ["dev"]
                         :repl-options {:init-ns user}
                         :env          {:elasticsearch-url "http://dev.chaperone:9200"
                                        :web-server-port   8080}}

               ;; profile specifically for compiling cljs, to remove unneccessary dependencies, and sinceA
               ;; it will blow up with dieter as it looks for the v8 native
               :cljs    {:dependencies [[org.clojure/clojurescript "0.0-2156"]
                                        [im.chit/purnam "0.3.0-SNAPSHOT"]
                                        [com.google.javascript/closure-compiler "v20131014"]
                                        [jayq "2.5.0"]]
                         :exclusions   [dieter http-kit compojure environ clj-time selmer clojurewerkz/elastisch]
                         :plugins      [[lein-cljsbuild "0.3.3"]]
                         :codox        {:sources    ["src-cljs"]
                                        :output-dir "doc/cljs"}
                         :cljsbuild    {
                                           :crossovers [chaperone.crossover]
                                           ;; compile test first, it's a faster feedback loop.
                                           :builds     [{:id             "test"
                                                         :source-paths   ["src-cljs", "test-cljs"]
                                                         :notify-command ["notify-send"]
                                                         :compiler       {:output-to     "resources/public/js/test/main.js"
                                                                          :output-dir    "resources/public/js/test/target"
                                                                          :optimizations :whitespace
                                                                          :pretty-print  true
                                                                          :source-map    "resources/public/js/test/main.js.map"
                                                                          }
                                                         },
                                                        {:id             "source"
                                                         ; The path to the top-level ClojureScript source directory:
                                                         :source-paths   ["src-cljs"]
                                                         :notify-command ["notify-send"]
                                                         ; The standard ClojureScript compiler options:
                                                         ; (See the ClojureScript compiler documentation for details.)
                                                         :compiler       {:output-to     "resources/public/js/main.js"
                                                                          :output-dir    "resources/public/js/target"
                                                                          :optimizations :whitespace
                                                                          :pretty-print  true
                                                                          :source-map    "resources/public/js/main.js.map"
                                                                          }
                                                         }]}}})