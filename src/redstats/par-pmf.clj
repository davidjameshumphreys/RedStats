(ns redstats.par-pmf
  (:require [redstats.frequency :as f]
            [clojure.core.reducers :as r]))

;; Population Mass Frequency (PMF).
;; The PMF is a normalised mapping of the probabilities; all of the
;; values should sum to 1.

;; The combiner will start with an empty map, because we are
;; processing the keys of another map there will not be any
;; intersection when performing the merge step -- that means we can
;; still merge the transient maps (in the frequency code we had to
;; use persistent maps to be able to use `merge-with`.

(defn pmf-combiner
  
  ([]
     (transient {}))
  ([m1 m2]
     (merge m1 m2)))

;; Two of the values to this process function will be known once the
;; frequencies have been calculated.  Population is the overall count
;; -- it will be used to normalise all of the values.
;; The `original-map` is all of the frequency counts`

(defn pmf-process [population original-map new-map next-value]
  (let [v (get original-map next-value)]
    (assoc! new-map next-value (/ v population))
    ))


(defn pmf [n coll]
  (let [[population frequencies] (f/freq n coll)]
    (persistent!
     (r/fold
      n
      pmf-combiner
      (partial pmf-process population frequencies)
      (keys frequencies)))))
