(ns bracketolojy.tournament
  (:require [clojure.math.combinatorics :as combo]
            [bracketolojy.log5 :as log5]
            [bracketolojy.util :refer :all]
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
  (let [a-comp-b (compare (:seed a) (:seed b))
        plus (fnil + 0)]
    (vector
     (assoc a :avg-pts (* (:weight a) (plus (when (pos? a-comp-b)
                                              upset-pts)
                                            pick-pts)))
     (assoc b :avg-pts (* (:weight b) (plus (when (neg? a-comp-b)
                                              upset-pts)
                                            pick-pts))))))

(defn ->tournament-teams
  "Transform a sequence of team data into a map of team name->tournament team data."
  [team-data]
  (->> team-data
       (remove (comp nil? :seed))
       (map #(vector (:name %) (assoc % :weight 1 :avg-pts 0 :expected-value 0)))
       (into {})))

(defn ->tournament-bracket
  "Transforms a bracket of team names and a map of tourament team data into a bracket of tournament teams."
  [bracket tournament-teams]
  (walk/postwalk-replace
    tournament-teams
    bracket))

(defn compute-matchup-dispatch [_ _ fields]
  (cond
    (and (coll? fields) (= (count fields) 2) (map? (first fields)) (map? (last fields)))
    :leaf

    (and (coll? fields) (= (count fields) 2) (coll? (first fields)) (coll? (last fields)))
    :branch))

(defmulti compute-matchup
  "Compute a matchup.  The first argument is an associative structure that gives the points per a correct pick
  for each round.  The second argument is an associative structure that gives the points per an upset pick for each
  round.  The third and final argument is the matchup data to perform the computation on."
  compute-matchup-dispatch)
(defmethod compute-matchup :default [_ _ matchup]
  matchup)
(defmethod compute-matchup :branch [pick-pts-fn upset-pts-fn fields]
  (dbg-v "")
  (dbg-v "branch")
  (dbg fields)
  (dbg (compute-matchup-dispatch pick-pts-fn upset-pts-fn fields))
  (let [upper-field (get-in fields [0 0])
        lower-field (get-in fields [1 0])
        round (log2 (reduce + (map count fields)))
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
      (#(hash-map :value % :left upper-field :right lower-field))
      dbg-v)))                               ;preserve the tree
(defmethod compute-matchup :leaf [pick-scoring upset-scoring pair]
  (dbg-v "")
  (dbg-v "leaf")
  (dbg pair)
  (dbg (compute-matchup-dispatch pick-scoring upset-scoring pair))
  (let [a (get pair 0)
        b (get pair 1)]
    (compute-matchup pick-scoring upset-scoring {:left {:value (list a)} :right {:value (list b)}})))

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

(defn- update-child-expected-value
  "Update the expected value of each team in the field using parent-team-map.  The expected value for each team in the
  field is the team's average points plus the expected value of the team in the parent-team-map."
  [parent-team-map [field children]]
  (vector
    (map
      #(update-in % [:expected-value] (fnil + 0 0)
        (get-in parent-team-map [(:name %) :expected-value])
        (:avg-pts %))
      field)
    children))

(defn- update-expected-value
  "For each child in children, update the expected value of the teams in that child's field.  The expected value for each team
  in a field is the team's average points plus the expected value of the team in the parent field."
  [[field children]]
  (vector
    field
    (let [parent-team-map (->>
                            field
                            (map #((juxt :name identity) %))
                            (into {}))]
      (map
        (partial update-child-expected-value parent-team-map)
        children))))

(defn- compute-expected-value-helper
  "Recursively traverse the zipper, updating each node with the expected value computation."
  [loc]
  (if (zip/end? loc)
    loc
    (recur (zip/next (zip/edit loc update-expected-value)))))

(defn compute-expected-value
  "Compute the expected value for each node in the tournament probabilites tree."
  [tp]
  (->>
    (update-child-expected-value nil tp)
    (zip/zipper
      (fn [[_ cn]]
        (or (vector? cn) (seq? cn)))
      (fn [[_ cn]]
        (seq cn))
      (fn [node cn]
        (assoc node 1 cn)))
    compute-expected-value-helper
    zip/root))

(defn predict-bracket
  "Predict the expected value for each team in each round of the tournament.  Bracket should be
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
    compute-expected-value))