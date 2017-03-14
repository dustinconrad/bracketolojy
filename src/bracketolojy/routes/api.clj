(ns bracketolojy.routes.api
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]
            [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket-data
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

(defn ->points-per-round-fn [pts-per-round]
  (let [pts (vec (concat [0] pts-per-round))]
    (partial get pts)))

(defn bracket [round-pts upset-pts]
  {:body (tourney/predict-bracket
           bracket-data
           (->points-per-round-fn round-pts)
           (->points-per-round-fn upset-pts)
           (data/get-kenpom-teams-bundled))})

(defroutes api-routes
  (context "/api" []
    (POST "/bracket" [roundPts upsetPts :as request]
      (bracket roundPts upsetPts))))
