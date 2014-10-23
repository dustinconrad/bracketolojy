(defproject bracketolojy "0.1.0-SNAPSHOT"
  :description "Use pythagorean expectation to spit out a bracket"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.combinatorics "0.0.8"]
                 [enlive "1.1.5"]
                 [lib-noir "0.9.4"]
                 [ring-server "0.3.1"]
                 [selmer "0.7.2"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.55"
                  :exclusions [com.keminglabs/cljx]]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.2"]
                 [noir-exception "0.2.2"]
                 [prone "0.6.0"]]

  :repl-options {:init-ns bracketolojy.repl}
  :jvm-opts ["-server"]
  :plugins [[lein-ring "0.8.13"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.5.5"]]
  :ring {:handler bracketolojy.handler/app
         :init    bracketolojy.handler/init
         :destroy bracketolojy.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.1"]
                        [pjstadig/humane-test-output "0.6.0"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}}
  :min-lein-version "2.0.0")
