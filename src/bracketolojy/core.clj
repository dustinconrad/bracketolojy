(ns bracketolojy.core
  (:require [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket
  [
   [ ;Midwest
    [[[[["Hampton"
         "Manhattan"]
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
        ["BYU"
         "Mississippi"]]

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
        ["Boise St."
         "Dayton"]]

       ["Oklahoma"
        "Albany"]]

      [["Michigan St."
        "Georgia"]

       ["Virginia"
        "Belmont"]]]
     ]

    [;South
     [[["Duke"
        ["North Florida"
         "Robert Morris"]]

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

(defn main []
  (tourney/predict-bracket
    bracket
    (partial get [0 0 1 2 4 8 12 16])
    (partial get [0 0 1 2 3 4 5 6])
    (data/get-kenpom-teams-bundled)))