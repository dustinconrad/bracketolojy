(ns bracketolojy.routes.api
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]
            [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket-data
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
