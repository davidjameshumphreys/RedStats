(ns redstats.par-cdf
  (:require [clojure.core.reducers :as r]))

;; A Cumulative Distribution Function CDF(x) gives the rank [0..1] for
;; x within the list.

;; This is a simple serial version of the CDF; find all of the values
;; in the list.
(defn naive-cdf [coll x]
  (let [xfn (partial < x)]
    (/
     (count (filter false? (map xfn coll)))
     (count coll))
    ))

;; In this aggregator for the CDF, 'next-value' is part of the
;; collection that is being processed; 'x' is the input to the CDF
;; function.
;; If nv <= X +1 else +0
;; Count the total items
(defn cdf-agg
  [x [true-pop total-pop] next-value]
  (let [add-value (if (<= next-value x) 1 0)]
    [(+ add-value true-pop) (inc total-pop)]))

;; Starting to see a pattern with these functions...
;; Should start to refactor the code.
(defn cdf-com
  ([] [0 0])
  ([[s1 c1] [s2 c2]]
     [(+ s1 s2) (+ c1 c2)]))

(defn par-cdf [n coll x]
  (let [[true-values total-pop]
        (r/fold
         n
         cdf-com
         (partial cdf-agg x)
         coll)]
    (/ true-values total-pop))
  )
