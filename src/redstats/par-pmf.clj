(ns redstats.par-pmf
  (:require [redstats.frequency :as f]
            [clojure.core.reducers :as r]))

;; Population Mass Frequency (PMF)
(defn pmf-combiner
  ([] (transient {}))
  ;; There /should/ be no intersection between the two maps; 
  ([m1 m2] (merge m1 m2)))

(defn pmf-process [population original-map new-map next-value]
  (let [v (get original-map next-value)]
    (assoc! new-map next-value (/ v population))
    ))

(defn pmf [n coll]
  (let [[population frequencies] (f/freq n coll)]
    
    ;;TODO: In the freq function the map is made persistent.  Could
    ;;there be a better way?
    ;; It goes through two phases of transience.
    (persistent!
     (r/fold
      n
      pmf-combiner
      (partial pmf-process population frequencies)
      (keys frequencies)))))
