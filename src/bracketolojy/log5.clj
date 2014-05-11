(ns bracketolojy.log5)

(defn log5
  "Returns the probability of a defeating b.  When applied to a or b,
  p-fn should return the pythagorean expectation."
  [p-fn a b]
  (let [pa (p-fn a)
        pb (p-fn b)]
    (/ (- pa (* pa pb))
       (- (+ pa pb)
          (* 2 pa pb)))))

(defn log5-p
  "Returns the probability of a defeating b, where pa is the pythogrean
  expectation of a, and pb is the pythagorean expectation of b."
  [pa pb]
  (log5 identity pa pb))

(defn log5-vs-field
  "Returns the probability of a defeating the field of b-coll.  When applied to
  the elements of b-coll, wt-fn should return the expected proportion of b in
  the field.  When applied to a or the elements of b-coll, p-fn should return
  the pythagorean expectation."
  [a b-coll wt-fn p-fn]
  (reduce
    (fn [acc b]
      (+ acc
        (* (wt-fn b) (log5 p-fn a b))))
    0
    b-coll))