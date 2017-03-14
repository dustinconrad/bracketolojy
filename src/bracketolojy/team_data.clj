(ns bracketolojy.team-data
  (:require [net.cgrand.enlive-html :as html]))

(defn ->team [name seed pe]
  {:name name
  :seed seed
  :pe pe})

(defn- parse-kenpom
  "Extract team seed and pythagorean win expectation from an html-resource sourced
  from http://kenpom.com/ and return a map of teams and their data."
  [html-resource]
  (let [rows (html/select html-resource [:#ratings-table :tr])]
    (->>
      rows
      (filter (comp (partial = 21) count :content))
      (#(html/let-select %
         [name [[:td (html/nth-child 2)] :a html/text]
          seed [[:td (html/nth-child 2)] :span html/text]
          adj-o [[:td (html/nth-child 6)] html/text]
          adj-d [[:td (html/nth-child 8)] html/text]
          adj-t [[:td (html/nth-child 10)] html/text]]
         (->team
           (first name)
           (when-let [seed-int (first seed)]
             (Integer/parseInt seed-int))
           (let [o (Math/pow (Double/parseDouble (first adj-o)) 11.5)
                 d (Math/pow (Double/parseDouble (first adj-d)) 11.5)]
             (/ o (+ o d))))))
      (remove (comp nil? :name)))))

(defn get-kenpom-teams
  "Get the team data from a kenpom.com html page.  Source should be a url to the page."
  [source]
  (parse-kenpom (html/html-resource source)))

(defn get-kenpom-teams-live
  "Fetch the live version of http://kenpom.com/ and return it's teams."
  []
  (get-kenpom-teams (java.net.URL "http://kenpom.com")))

(defn get-kenpom-teams-bundled
  "Fetch the bundled version of http://kenpom.com/ from the resource directory and return
  it's teams"
  []
  (get-kenpom-teams (clojure.java.io/resource "kenpom.html")))