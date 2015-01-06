(ns bracketolojy.routes.api
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]
            [bracketolojy.tournament :as tourney]
            [bracketolojy.team-data :as data]))

(def bracket-data
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

(defn bracket []
  {:body (tourney/predict-bracket
           bracket-data
           (partial get [0 1 2 4 8 12 16])
           (partial get [0 1 2 3 4 5 6])
           (data/get-kenpom-teams-bundled))})

(defroutes api-routes
           (context "/api" []
                    (GET "/bracket" [] (bracket))))
