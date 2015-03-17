(ns bracketolojy.util
  (:require [noir.io :as io]
            [markdown.core :as md]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (md/md-to-html-string (io/slurp-resource filename)))

(defmacro dbg [x]
  `(let [x# ~x]
     (println "dbg:" '~x "=" x#)
     x#))

(defmacro dbg-v [x]
  `(let [x# ~x]
     (println "dbg:" x#)
     x#))

(defmacro trace [s x]
  `(let [x# ~x]
     (println "trace: " ~s)
     x#))