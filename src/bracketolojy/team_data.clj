(ns bracketolojy.team-data
  (:require [net.cgrand.enlive-html :as html]))

(defrecord Team [name seed pe])

(defn- parse-kenpom [html-resource]
  "Extract team seed and pythagorean win expectation from an html-resource sourced
  from http://kenpom.com/ and return a map of teams and their data."
  (let [rows (html/select html-resource [:#ratings-table :tr])]
    (->>
      rows
      (filter (comp (partial = 21) count :content))
      (#(html/let-select %
         [name [[:td (html/nth-child 2)] :a html/text]
          seed [[:td (html/nth-child 2)] :span html/text]
          pe [[:td (html/nth-child 5)] html/text]]
         (->Team
           (first name)
           (if-let [seed-int (first seed)]
             (Integer/parseInt seed-int)
             nil)
           (Double/parseDouble (first pe)))))
      (remove (comp nil? :name)))))

(defn get-kenpom-teams [source]
  "Get the team data from a kenpom.com html page.  Source should be a url to the page."
  (parse-kenpom (html/html-resource source)))

(defn get-kenpom-teams-live []
  "Fetch the live version of http://kenpom.com/ and return it's teams."
  (get-kenpom-teams (java.net.URL "http://kenpom.com")))

(defn get-kenpom-teams-bundled []
  "Fetch the bundled version of http://kenpom.com/ from the resource directory and return
  it's teams"
  (get-kenpom-teams (clojure.java.io/resource "kenpom.html")))