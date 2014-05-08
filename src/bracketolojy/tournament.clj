(ns bracketolojy.tournament
  (:require [clojure.math.combinatorics :as combo]
            [bracketolojy.log5 :as log5]
            [bracketolojy.team-data :as data]
            [clojure.walk :as walk]
            [clojure.zip :as zip]))

(defrecord Team [name seed pe weight avg-pts future-pts])

(defn- log2 [x]
  "The log base 2 of x, rounded to the nearest integer."
  (Math/round
    (/ (Math/log x)
       (Math/log 2))))

(defn weighted-pairing-log5 [[a b]]
  "Compute and update the chance that Team a and Team b will advance when playing against each other,
  weighted by the chance the matchup will occur."
  (let [pe-avb (log5/log5 :pe a b)]
    (vector
      (update-in a [:weight] * (:weight b) pe-avb)
      (update-in b [:weight] * (:weight a) (- 1 pe-avb)))))

(defn weighted-pairing-pts [pick-pts upset-pts [a b]]
  "Compute the expected point outcome Team a and Team b will yield when playing each other, using the
  the current weights for each team."
  (vector
    (assoc a :avg-pts (* (:weight a) (+ pick-pts
                                        (if (> (:seed a) (:seed b))
                                          upset-pts
                                          0))))
    (assoc b :avg-pts (* (:weight b) (+ pick-pts
                                        (if (> (:seed b) (:seed a))
                                          upset-pts
                                          0))))))

(defmulti compute-matchup
  "Compute a matchup.  The first argument is an associative structure that gives the points per a correct pick
  for each round.  The second argument is an associative structure that gives the points per an upset pick for each
  round.  The third and final argument is the matchup data to perform the computation on."
  (fn [_ _ field]
    (cond
      (and (coll? field) (= (count field) 2) (record? (first field)) (record? (last field)))
      :leaf
      (and (coll? field) (= (count field) 2) (coll? (first field)) (coll? (last field)))
      :branch)))
(defmethod compute-matchup :default [_ _ matchup]
  matchup)
(defmethod compute-matchup :branch [pick-scoring upset-scoring [[upper-field] [lower-field] :as fields]]
  (let [round (log2 (reduce + (map count fields)))
        pick-pts (get pick-scoring round)
        upset-pts (get upset-scoring round)]
    (->>
      (combo/cartesian-product upper-field lower-field)     ;all head to head games
      (map weighted-pairing-log5)                           ;calculate win % per game
      (mapcat (partial weighted-pairing-pts pick-pts upset-pts)) ;calculate expected pts per game
      (reduce                                               ;aggregate game results together
        #(let [name (:name %2)]
            (if (contains? %1 name)
              (-> (update-in %1 [name :weight] + (:weight %2))
                  (update-in [name :avg-pts] + (:avg-pts %2)))
              (assoc %1 name %2)))
        {})
      vals                                                  ;get the aggregation
      (#(vector % fields)))))                               ;preserve the tree
(defmethod compute-matchup :leaf [pick-scoring upset-scoring [a b]]
  (compute-matchup pick-scoring upset-scoring [[(list a)] [(list b)]]))

(defn teams->tournament-teams [teams]
  "Transform a list of teams into a map of team name->team data."
  (->> teams
    (remove (comp nil? :seed))
    (map #(vector (:name %) (assoc (map->Team %) :weight 1 :avg-pts 0 :future-pts 0)))
    (into {})))

(defn ->tournament-bracket [bracket teams]
  "Transforms a bracket of team names and a map of team data into a bracket with team data."
  (walk/postwalk-replace
    teams
    bracket))

(defn tournament-probabilities [pick-scoring upset-scoring tournament-bracket]
  "Computes the probability of the various outcomes of a tournament.  Calculates the first round, then
  uses the results from that to calculate the second round, ad infinitum until all rounds are calculated."
  (walk/postwalk
    (partial compute-matchup pick-scoring upset-scoring)
    tournament-bracket))

(defn update-future-pts [[field children] parent-team-map]
  "Update the future-pts of each team in the field using parent-team-map.  The future-pts for each team in the
  field is the team's average points plus the future-pts of the team in the parent-team-map."
  (vector
    (map
      #(update-in % [:future-pts] +
        (get-in parent-team-map [(:name %) :future-pts] 0)
        (:avg-pts %))
      field)
    children))

(defn- compute-future-pts-helper [loc parent-team-map]
  (if (zip/end? loc)
    loc
    (let [updated-loc (zip/edit loc update-future-pts parent-team-map)]
      (recur
        (zip/next updated-loc)
        (->>                                                ;create next parent map
          (zip/node updated-loc)
          first
          (map #((juxt :name identity) %))
          (into {}))))))

(defn compute-future-pts [tp]
  (->
    (zip/zipper
      (fn [[_ cn]]
        (vector? cn))
      (fn [[_ cn]]
        cn)
      (fn [node cn]
        (assoc node 1 cn))
      tp)
    (compute-future-pts-helper nil)
    zip/root))

;(def bracket
;  [[[["Florida"
;      "Albany"]
;
;     ["Colorado"
;      "Pittsburgh"]]
;
;    [["VCU"
;      "Stephen F. Austin"]
;
;     ["UCLA"
;      "Tulsa"]]]
;
;   [[["Ohio St."
;      "Dayton"]
;
;     ["Syracuse"
;      "Western Michigan"]]
;
;    [["New Mexico"
;      "Stanford"]
;
;     ["Kansas"
;      "Eastern Kentucky"]]]])

;(def bracket
;  [[["Florida"
;     "Albany"]
;
;    ["Colorado"
;     "Pittsburgh"]]
;
;   [["VCU"
;     "Stephen F. Austin"]
;
;    ["UCLA"
;     "Tulsa"]]]
;  )
;
(def bracket
  [["VCU"
    "Stephen F. Austin"]

   ["UCLA"
    "Tulsa"]]
  )


;(println
;  (tournament-probabilities
;    [0 1 2 4 8 12 16]
;    [0 1 2 3 4 5 6]
;    (tournament-bracket
;      bracket
;      (to-tournament-teams (data/get-kenpom-teams-bundled)))))

(println
  (->>
    (tournament-probabilities
      [0 1 2 4 8 12 16]
      [0 1 2 3 4 5 6]
      (->tournament-bracket
        bracket
        (teams->tournament-teams (data/get-kenpom-teams-bundled))))
    compute-future-pts
   ))

