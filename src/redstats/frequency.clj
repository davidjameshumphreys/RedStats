(ns redstats.frequency
  (:require [clojure.core.reducers :as r]))

(defn fr-agg [[population
               running-count]
              next-value]
  [(inc population)
   (assoc running-count next-value (inc (get running-count next-value 0)))]
  )

(defn fr-com
  ([] [0 {}])
  ([[p1 m1] [p2 m2]]
     [(+ p1 p2) (merge-with + m1 m2)]
     )
  )

;; A function that gathers the frequencies and total population size.
;; This would be useful for building a PMF
(defn freq [n coll]
  (let [[population frequencies]
        (r/fold
         n
         fr-com
         fr-agg
         coll)]
    [population frequencies]
    ))
