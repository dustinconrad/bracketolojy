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
;
;(println
;  (tourney/predict-bracket
;    bracket
;    [0 1 2 4 8 12 16]
;    [0 1 2 3 4 5 6]
;    (data/get-kenpom-teams-bundled)))

(defn mindex [vals]
  (->>
    (reductions
      min
      (reverse vals))
    reverse
    vec))

(defn -main [& args]
  (let [n (read)
        vals (repeatedly n read)]
    (->>
      (map
        -
        (mindex vals)
        vals)
      (apply min)
      println)))
