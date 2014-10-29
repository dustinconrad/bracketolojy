(ns bracketolojy.core
  (:require [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

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

(defn- main []
  (tourney/predict-bracket
    bracket
    (partial get [0 1 2 4 8 12 16])
    (partial get [0 1 2 3 4 5 6])
    (data/get-kenpom-teams-bundled)))
