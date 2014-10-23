(ns bracketolojy.routes.home
  (:require [compojure.core :refer :all]
            [bracketolojy.layout :as layout]
            [bracketolojy.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn bracket []
  (layout/render "bracket.html"))

(defroutes home-routes
           (GET "/" [] (home-page))
           (GET "/about" [] (about-page))
           (GET "/bracket" [] (bracket)))