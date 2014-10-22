(ns bracketolojy.tournament-test
  (:require [clojure.test :refer :all]
            [bracketolojy.tournament :refer :all]
            [bracketolojy.log5 :as log5]))

(defn ->tournament-team [name seed pe weight avg-pts]
  {:name name
  :seed seed
  :pe pe
  :weight weight
  :avg-pts avg-pts})

(deftest test-weighted-pairing-log5
  (testing "Testing with various weights"
    (are [a b a-weight b-weight]
      (= (weighted-pairing-log5
           ;name seed pe weight avg-pts
           [(->tournament-team "a" \_ a a-weight \_) (->tournament-team "b" \_ b b-weight \_)])
         [(->tournament-team "a" \_ a (* (log5/log5-p a b) a-weight b-weight) \_)
          (->tournament-team "b" \_ b (* (log5/log5-p b a) a-weight b-weight) \_)])
      9/10 4/10 1 1
      2/10 3/10 1 1
      9/10 4/10 7/10 1
      2/10 3/10 1/10 1
      9/10 4/10 1 7/10
      2/10 3/10 1 1/10
      1/10 2/10 3/10 5/10
      7/10 3/10 5/10 2/10)))

(deftest test-weighted-pairing-pts
  (testing "Testing with no weight, seeds, pick points, and upset points."
    (are [a-seed b-seed pick-pts upset-pts a-pts b-pts]
      (= (weighted-pairing-pts pick-pts upset-pts
                               ;name seed pe weight avg-pts
                               [(->tournament-team "a" a-seed \_ 1 \_) (->tournament-team "b" b-seed \_ 1 \_)])
         [(->tournament-team "a" a-seed \_ 1 a-pts)
          (->tournament-team "b" b-seed \_ 1 b-pts)])
      1 16 1 1 1 2
      1 1 1 1 1 1
      2 3 4 2 4 6))
  (testing "Testing with various weights, seeds, pick points, and upset points."
    (are [a-weight b-weight a-seed b-seed pick-pts upset-pts]
      (= (weighted-pairing-pts pick-pts upset-pts
           ;name seed pe weight avg-pts
           [(->tournament-team "a" a-seed \_ a-weight \_) (->tournament-team "b" b-seed \_ b-weight \_)])
         [(->tournament-team "a" a-seed \_ a-weight (* a-weight (+ pick-pts (if (> a-seed b-seed) upset-pts 0))))
          (->tournament-team "b" b-seed \_ b-weight (* b-weight (+ pick-pts (if (> b-seed a-seed) upset-pts 0))))])
      1 1 1 16 1 1
      5/10 5/10 2 3 8 5)))
