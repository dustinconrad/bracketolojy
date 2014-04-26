(ns bracketolojy.team-data
  (:require [net.cgrand.enlive-html :as html]))

(defrecord Team [name seed pe])

(defn parse-kenpom [html-resource]
  (let [rows (html/select html-resource [:#ratings-table :tr])]
    (->>
      rows
      (#(html/let-select %
         [name [[:td (html/nth-child 2)] :a html/text]
          seed [[:td (html/nth-child 2)] :span html/text]
          pe [[:td (html/nth-child 5)] html/text]]
         (->Team (first name) (first seed) (first pe))))
      (remove #(nil? (:seed %))))))

(defn fetch-kenpom-live []
  (html/html-resource (java.net.URL. "http://kenpom.com/")))

(defn fetch-kenpom-bundled []
  (html/html-resource (clojure.java.io/resource "kenpom.html")))

(println
  (parse-kenpom (fetch-kenpom-bundled)))

;(println
;  (html/html-resource (java.net.URL. "http://kenpom.com/")))


;(map (fn [[_ {[{[name] :content} & _] :content} _ _ {} & more]]
;       (apply vector name pe more)))