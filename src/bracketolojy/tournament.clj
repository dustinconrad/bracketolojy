(ns bracketolojy.tournament
  (:require [clojure.math.combinatorics :as combo]
            [bracketolojy.log5 :as log5]
            [bracketolojy.team-data :as data]
            [clojure.walk :as walk]
            [clojure.zip :as zip]))

(defn- log2
  "The log base 2 of x, rounded to the nearest integer."
  [x]
  (Math/round
    (/ (Math/log x)
       (Math/log 2))))

(defn weighted-pairing-log5
  "Compute and update the chance that Team a and Team b will advance when playing against each other,
  weighted by the chance the matchup will occur."
  [a b]
  (let [pe-avb (log5/log5 :pe a b)]
    (vector
      (update-in a [:weight] * (:weight b) pe-avb)
      (update-in b [:weight] * (:weight a) (- 1 pe-avb)))))

(defn weighted-pairing-pts
  "Compute the expected point outcome Team a and Team b will yield when playing each other, using the
  the current weights for each team."
  [pick-pts upset-pts a b]
  (vector
    (assoc a :avg-pts (* (:weight a) (+ pick-pts
                                        (if (> (:seed a) (:seed b))
                                          upset-pts
                                          0))))
    (assoc b :avg-pts (* (:weight b) (+ pick-pts
                                        (if (> (:seed b) (:seed a))
                                          upset-pts
                                          0))))))

(defn ->tournament-teams
  "Transform a sequence of team data into a map of team name->tournament team data."
  [team-data]
  (->> team-data
       (remove (comp nil? :seed))
       (map #(vector (:name %) (assoc % :weight 1 :avg-pts 0 :future-pts 0)))
       (into {})))

(defn ->tournament-bracket
  "Transforms a bracket of team names and a map of tourament team data into a bracket of tournament teams."
  [bracket tournament-teams]
  (walk/postwalk-replace
    tournament-teams
    bracket))

(defmulti #^{:private true} compute-matchup
  "Compute a matchup.  The first argument is an associative structure that gives the points per a correct pick
  for each round.  The second argument is an associative structure that gives the points per an upset pick for each
  round.  The third and final argument is the matchup data to perform the computation on."
  (fn [_ _ field]
    (cond
      (and (coll? field) (= (count field) 2) (map? (first field)) (map? (last field)))
      :leaf
      (and (coll? field) (= (count field) 2) (coll? (first field)) (coll? (last field)))
      :branch)))
(defmethod compute-matchup :default [_ _ matchup]
  matchup)
(defmethod compute-matchup :branch [pick-pts-fn upset-pts-fn [[upper-field] [lower-field] :as fields]]
  (let [round (log2 (reduce + (map count fields)))
        pick-pts (pick-pts-fn round)
        upset-pts (upset-pts-fn round)]
    (->>
      (combo/cartesian-product upper-field lower-field)     ;all head to head games
      (map (partial apply weighted-pairing-log5))           ;calculate win % per game
      (mapcat (partial apply weighted-pairing-pts pick-pts upset-pts)) ;calculate expected pts per game
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

(defn tournament-probabilities
  "Computes the probability of the various outcomes of a tournament.  Calculates the first round, then
  uses the results from that to calculate the second round, ad infinitum until all rounds are calculated.
  The first argument is an associative structure that gives the points per a correct pick for each round.
  The second argument is an associative structure that gives the points per an upset pick for each round.
  The third and final argument is the tournament bracket."
  [pick-scoring upset-scoring tournament-bracket]
  (walk/postwalk
    (partial compute-matchup pick-scoring upset-scoring)
    tournament-bracket))

(defn- update-child-future-pts
  "Update the future-pts of each team in the field using parent-team-map.  The future-pts for each team in the
  field is the team's average points plus the future-pts of the team in the parent-team-map."
  [parent-team-map [field children]]
  (vector
    (map
      #(update-in % [:future-pts] +
        (get-in parent-team-map [(:name %) :future-pts] 0)
        (:avg-pts %))
      field)
    children))

(defn- update-future-pts
  "For each child in children, update the future-pts of the teams in that child's field.  The future-pts for each team
  in a field is the team's average points plus the future-pts of the team in the parent field."
  [[field children]]
  (vector
    field
    (let [parent-team-map (->>
                            field
                            (map #((juxt :name identity) %))
                            (into {}))]
      (map
        (partial update-child-future-pts parent-team-map)
        children))))

(defn- compute-future-pts-helper
  "Recursively traverse the zipper, updating each node with the future points computation."
  [loc]
  (if (zip/end? loc)
    loc
    (recur (zip/next (zip/edit loc update-future-pts)))))

(defn compute-future-pts
  "Compute the future points for each node in the tournament probabilites tree."
  [tp]
  (->>
    (update-child-future-pts nil tp)
    (zip/zipper
      (fn [[_ cn]]
        (or (vector? cn) (seq? cn)))
      (fn [[_ cn]]
        (seq cn))
      (fn [node cn]
        (assoc node 1 cn)))
    compute-future-pts-helper
    zip/root))

(defn predict-bracket
  "Predict the expected future points for each team in each round of the tournament.  Bracket should be
  a bracket of team names in a tree structure.  pick-scoring is an associative structure that gives the points
  per a correct pick for each round.  upset-scoring is an associative structure that gives the points per an
  upset pick for each round.  team-data is a seq of team data."
  [bracket pick-scoring upset-scoring team-data]
  (->>
    (->tournament-bracket
      bracket
      (->tournament-teams team-data))
    (tournament-probabilities
      pick-scoring
      upset-scoring)
    compute-future-pts))