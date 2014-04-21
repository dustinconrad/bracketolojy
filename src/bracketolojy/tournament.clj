(ns bracketolojy.tournament
  (:require [clojure.math.combinatorics :as combo]
            [bracketolojy.log5 :as log5]
            [clojure.walk :as walk]))

(defrecord Team [name seed pe weight avg-pts])

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
          "Compute a matchup."
          (fn [pick-scoring upset-scoring field]
            (and (coll? field) (= (count field) 2)
                 (coll? (first field)) (coll? (last field)))))
(defmethod compute-matchup :default [_ _ matchup]
  matchup)
(defmethod compute-matchup true [pick-scoring upset-scoring [upper-field lower-field :as fields]]
  (let [round (count upper-field)
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
      vals
;      (#(vector % fields))
      )))

(def sample-data
  [[[(->Team "Florida" 1 0.9502 1 \_)]
    [(->Team "Albany" 16 0.4698 1 \_)]]

   [[(->Team "Colorado" 8 0.7156 1 \_)]
    [(->Team "Pittsburgh" 9 0.8848 1 \_)]]])

;(def sample-data
;  [[(->Team "Colorado" 8 0.7156 1 \_)]
;   [(->Team "Pittsburgh" 9 0.8848 1 \_)]])

(println
  (let [result (walk/postwalk
          (partial compute-matchup [0 1 2] [0 1 2])
          sample-data)
        ;sum (->>
        ;      result
        ;      (map :weight)
        ;      (reduce +))
        ]
    result))

