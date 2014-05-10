(ns bracketolojy.core
  (:require [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def bracket
  [[[["Florida"
      "Albany"]

     ["Colorado"
      "Pittsburgh"]]

    [["VCU"
      "Stephen F. Austin"]

     ["UCLA"
      "Tulsa"]]]

   [[["Ohio St."
      "Dayton"]

     ["Syracuse"
      "Western Michigan"]]

    [["New Mexico"
      "Stanford"]

     ["Kansas"
      "Eastern Kentucky"]]]])

(println
  (tourney/predict-bracket
    bracket
    [0 1 2 4 8 12 16]
    [0 1 2 3 4 5 6]
    (data/get-kenpom-teams-bundled)))
