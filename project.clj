(defproject bracketolojy "0.1.0-SNAPSHOT"
  :description "Use pythagorean expectation to spit out a bracket"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot bracketolojy.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
