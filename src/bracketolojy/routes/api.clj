(ns bracketolojy.routes.api
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]))

(defn region-data []
  {:west {"Arizona" 1
          "Weber St." 16
          "Gonzaga" 8
          "Oklahoma St." 9
          "Oklahoma" 5
          "North Dakota St." 12
          "San Diego St." 4
          "New Mexico St." 13
          "Baylor" 6
          "Nebraska" 11
          "Creighton" 3
          "LA-Lafayette" 14
          "Oregon" 7
          "BYU" 10
          "Wisconsin" 2
          "American U" 15}
   :midwest {"Wichita St." 1
           "Cal Poly" 16
           "Kentucky" 8
           "Kansas St" 9
           "St. Louis" 5
           "NC State" 12
           "Louisville" 4
           "Manhattan" 13
           "Massachusetts" 6
           "Tennessee" 11
           "Duke" 3
           "Mercer" 14
           "Texas" 7
           "Arizona State" 10
           "Michigan" 2
           "Wofford" 15}
   :east {"Virginia" 1
          "Coast Carolina" 16
          "Memphis" 8
          "G. Washington" 9
          "Cincinnati" 5
          "Harvard" 12
          "Michigan St." 4
          "Delaware" 13
          "North Carolina" 6
          "Providence" 11
          "Iowa State" 3
          "N Carolina Central" 14
          "Connecticut" 7
          "St. Joseph's" 10
          "Villanova" 2
          "Milwaukee" 15}
   :south {"Florida" 1
           "Albany" 16
           "Colorado" 8
           "Pittsburgh" 9
           "VCU" 5
           "Stephen F. Austin" 12
           "UCLA" 4
           "Tulsa" 13
           "Ohio State" 6
           "Dayton" 11
           "Syracuse" 3
           "Western Michigan" 14
           "New Mexico" 7
           "Stanford" 10
           "Kansas" 2
           "Eastern Kentucky" 15}})

(defn regions []
  {:body (region-data)})

(defn about-page []
  (layout/render "about.html"))


(defroutes api-routes
           (GET "/regions" [] (regions)))
