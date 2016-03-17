(ns bracketolojy.team-data-test
  (:require [clojure.test :refer :all]
            [bracketolojy.team-data :refer :all]))

(deftest test-bundled-data
  (testing "testing bundled data"
    (let [team-map (get-kenpom-teams-bundled)
          team-map-seq (doall team-map)]
      (->> team-map
           (map (fn [{:keys [name seed pe]}]
              (is (some? name))
              (is (some? pe))))
           doall)
      (->> team-map
           (filter (fn [{:keys [seed]}]
                     (some? seed)))
           (filter (fn [{:keys [seed]}]
                  (integer? seed)))
           (#(is (= 68 (count %))))))))