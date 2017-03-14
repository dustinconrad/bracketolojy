(ns bracketolojy.core
  (:require [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket
  [
   [ ;East
    [[[["Villanova"
        "New Orleans"]

       ["Wisconsin"
        "Virginia Tech"]]

      [["Virginia"
        "UNC Wilmington"]

       ["Florida"
        "East Tennessee St."]]]

     [[["SMU"
        "Providence"]

       ["Baylor"
        "New Mexico St."]]

      [["South Carolina"
        "Marquette"]

       ["Duke"
        "Troy"]]]]


    [;West
     [[["Gonzaga"
        "South Dakota St."]

       ["Northwestern"
        "Vanderbilt"]]

      [["Notre Dame"
        "Princeton"]

       ["West Virginia"
        "Bucknell"]]]

     [[["Maryland"
        "Xavier"]

       ["Florida St."
        "Florida Gulf Coast"]]

      [["Saint Mary's"
        "VCU"]

       ["Arizona"
        "North Dakota"]]]]
    ]

   [
    [;Midwest
     [[["Kansas"
        "North Carolina Central"]

       ["Miami FL"
        "Michigan St."]]

      [["Iowa St."
        "Nevada"]

       ["Purdue"
        "Vermont"]]]

     [[["Creighton"
        "Rhode Island"]

       ["Oregon"
        "Iona"]]

      [["Michigan"
        "Oklahoma St."]

       ["Louisville"
        "Jacksonville St."]]]
     ]

    [;South
     [[["North Carolina"
        "Texas Southern"]

       ["Arkansas"
        "Seton Hall"]]

      [["Minnesota"
        "Middle Tennessee"]

       ["Butler"
        "Winthrop"]]]

     [[["Cincinnati"
        "Kansas St."]

       ["UCLA"
        "Kent St."]]

      [["Dayton"
        "Wichita St."]

       ["Kentucky"
        "Northern Kentucky"]]]
     ]
    ]
   ])

(defn main []
  (tourney/predict-bracket
    bracket
    (partial get [0 2 3 5 8 13 21])
    (partial get [0 4 4 2 1 1 1])
    (data/get-kenpom-teams-bundled)))

(defn check-bracket []
  (let [team-data (set (keys (tourney/->tournament-teams (data/get-kenpom-teams-bundled))))
        bracket-teams (set (flatten bracket))]
    [(clojure.set/difference bracket-teams team-data)
     (clojure.set/difference team-data bracket-teams)]))