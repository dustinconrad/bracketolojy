(ns bracketolojy.core
  (:require [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket
  [
   [ ;South
    [[[["Kansas"
        "Austin Peay"]

       ["Colorado"
        "Connecticut"]]

      [["Maryland"
        "South Dakota St."]

       ["California"
        "Hawaii"]]]

     [[["Arizona"
        "Wichita St."]

       ["Miami FL"
        "Buffalo"]]

      [["Iowa"
        "Temple"]

       ["Villanova"
        "UNC Asheville"]]]]


    [;West
     [[["Oregon"
        "Holy Cross"]

       ["Saint Joseph's"
        "Cincinnati"]]

      [["Baylor"
        "Yale"]

       ["Duke"
        "UNC Wilmington"]]]

     [[["Texas"
        "Northern Iowa"]

       ["Texas A&M"
        "Green Bay"]]

      [["Oregon St."
        "VCU"]

       ["Oklahoma"
        "Cal St. Bakersfield"]]]]
    ]

   [
    [;East
     [[["North Carolina"
        "Florida Gulf Coast"]

       ["USC"
        "Providence"]]

      [["Indiana"
        "Chattanooga"]

       ["Kentucky"
        "Stony Brook"]]]

     [[["Notre Dame"
        "Michigan"]

       ["West Virginia"
        "Stephen F. Austin"]]

      [["Wisconsin"
        "Pittsburgh"]

       ["Xavier"
        "Weber St."]]]
     ]

    [;Midwest
     [[["Virginia"
        "Hampton"]

       ["Texas Tech"
        "Butler"]]

      [["Purdue"
        "Arkansas Little Rock"]

       ["Iowa St."
        "Iona"]]]

     [[["Seton Hall"
        "Gonzaga"]

       ["Utah"
        "Fresno St."]]

      [["Dayton"
        "Syracuse"]

       ["Michigan St."
        "Middle Tennessee"]]]
     ]
    ]
   ])

(defn main []
  (tourney/predict-bracket
    bracket
    (partial get [0 1 2 4 8 16 32])
    (partial get [0 0 0 0 0 0 0])
    (data/get-kenpom-teams-bundled)))

(defn check-bracket []
  (let [team-data (set (keys (tourney/->tournament-teams (data/get-kenpom-teams-bundled))))
        bracket-teams (set (flatten bracket))]
    [(clojure.set/difference bracket-teams team-data)
     (clojure.set/difference team-data bracket-teams)]))