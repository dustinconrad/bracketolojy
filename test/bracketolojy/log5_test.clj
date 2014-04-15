(ns bracketolojy.log5-test
  (:require [clojure.test :refer :all]
            [bracketolojy.log5 :refer :all]))

(deftest test-log5-p-output
  (are [pa pb expected]
    (= (log5-p pa pb) expected)
    6/10 4/10 9/13
    0.6 0.4 0.6923076923076923
    0.7883 0.7415 0.5648646670793241))

(deftest test-log5-output
  (testing "testing identity"
    (are [pa pb expected]
      (= (log5 pa pb identity) expected)
      6/10 4/10 9/13
      0.6 0.4 0.6923076923076923
      0.7883 0.7415 0.5648646670793241))
  (testing "using functions"
    (are [a b p-fn expected]
      (= (log5 a b p-fn) expected)
      '(:first 6/10) [:first 4/10] second 9/13
      6 4 #(/ % 10) 9/13
      {:pe 0.7883} {:pe 0.7415} #(:pe %) 0.5648646670793241)))

(deftest test-log5-vs-field-output
  (testing "testing equal weights and pythagorean expectations"
    (let [a {:pe 6/10}
          b-coll (repeat 3 {:pe 4/10})
          wt-fn (constantly (/ (count b-coll)))
          p-fn #(:pe %)]
      (is (= (log5-vs-field a b-coll wt-fn p-fn) (log5 a {:pe 4/10} p-fn))))))
