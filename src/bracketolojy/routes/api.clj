(ns bracketolojy.routes.api
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]
            [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket-data
  [
   [ ;Midwest
    [[[["Hampton"
        "Kentucky"]

       ["Cincinnati"
        "Purdue"]]

      [["West Virginia"
        "Buffalo"]

       ["Maryland"
        "Valparaiso"]]]

     [[["Butler"
        "Texas"]

       ["Notre Dame"
        "Northeastern"]]

      [["Wichita St."
        "Indiana"]

       ["Kansas"
        "New Mexico St."]]]]


    [;West
     [[["Wisconsin"
        "Coastal Carolina"]

       ["Oregon"
        "Oklahoma St."]]

      [["Arkansas"
        "Wofford"]

       ["North Carolina"
        "Harvard"]]]

     [[["Xavier"
        "Mississippi"]

       ["Baylor"
        "Georgia St."]]

      [["VCU"
        "Ohio St."]

       ["Arizona"
        "Texas Southern"]]]]
    ]

   [
    [;East
     [[["Villanova"
        "Lafayette"]

       ["North Carolina St."
        "LSU"]]

      [["Northern Iowa"
        "Wyoming"]

       ["Louisville"
        "UC Irvine"]]]

     [[["Providence"
        "Dayton"]

       ["Oklahoma"
        "Albany"]]

      [["Michigan St."
        "Georgia"]

       ["Virginia"
        "Belmont"]]]
     ]

    [;South
     [[["Duke"
        "Robert Morris"]

       ["San Diego St."
        "St. John's"]]

      [["Utah"
        "Stephen F. Austin"]

       ["Georgetown"
        "Eastern Washington"]]]

     [[["SMU"
        "UCLA"]

       ["Iowa St."
        "UAB"]]

      [["Iowa"
        "Davidson"]

       ["Gonzaga"
        "North Dakota St."]]]
     ]
    ]
   ])

(defn ->points-per-round-fn [pts-per-round]
  (partial get (concat [0] pts-per-round)))

(defn bracket [round-pts upset-pts]
  (println round-pts)
  (println upset-pts)
  {:body (tourney/predict-bracket
           bracket-data
           (partial get [0 1 2 4 8 16 32])
           (partial get [0 0 0 0 0 0 0])
           (data/get-kenpom-teams-bundled))})

(defroutes api-routes
  (context "/api" []
    (POST "/bracket" [roundPts upsetPts :as request]
      (println request)
      (bracket roundPts upsetPts))))
