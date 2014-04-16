(ns bracketolojy.log5)

(defn log5 [a b p-fn]
  "Returns the probability of a defeating b.  When applied to a or b,
  p-fn should return the pythagorean expectation."
  (let [pa (p-fn a)
        pb (p-fn b)]
    (/ (- pa (* pa pb))
       (- (+ pa pb)
          (* 2 pa pb)))))

(defn log5-p [pa pb]
  "Returns the probability of a defeating b, where pa is the pythogrean
  expectation of a, and pb is the pythagorean expectation of b."
  (log5 pa pb identity))

(defn log5-vs-field [a b-coll wt-fn p-fn]
  "Returns the probability of a defeating the field of b-coll.  When applied to
  the elements of b-coll, wt-fn should return the expected proportion of b in
  the field.  When applied to a or the elements of b-coll, p-fn should return
  the pythagorean expectation."
  (reduce
    (fn [acc b]
      (+ acc
        (* (wt-fn b) (log5 a b p-fn))))
    0
    b-coll))