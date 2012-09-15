(ns redstats.parallel_variance
  (require [clojure.core.reducers :as r]
           [redstats.core :as rcore :only [par-mean]]))

;; This will be a simple attempt to parallelise some parts of
;; computing the variance of a list.  It will need two phases of
;; analysing the list:
;; The first will get the mean (which should be computable in
;; parallel).
;; The second will use the mean to calculate the square distances from
;; the mean (which can then be summed).

;; When we have the mean we will need to calculate the
;; mean-square-difference (MSD) for each value n in the list.
(defn mean-square-difference [mean n]
  (let [d (- mean n)]
    (* d d)))

;; We aggregate over the list once the mean is known.
;; We add the MSD for next-value into the running sum and increment
;; the counter.
(defn variance-aggregation
  [mean [running-sum running-count] next-value]
  (let [s (+ running-sum (mean-square-difference mean next-value))
        c (inc running-count)]
    [s c]))

;; The 0-arity value will be used as the seed value.
;; The 2-arity function will be used to combine sublists together.
(defn variance-combiner
  ([] [0 0])
  ([[s1 c1] [s2 c2]]
     [(+ s1 s2) (+ c1 c2)]))

(defn par-variance [n coll]
  (let [m (rcore/par-mean n coll)
        [var-sum c] (r/fold
           n
           variance-combiner
           (partial variance-aggregation m)
           coll)
        ]
    (/ var-sum c)))