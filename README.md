RedStats
========

Some basic descriptive stats using Clojure's reducers for speed

Mean
----
The `core/par-mean` function can calculate the mean of a collection using the new reducers.

Variance
----
The `parallel-variance/par-variance` function can calculate the variance of a collection.
It will process the collection twice; once to calculate the mean (as above); and finally to calcualte the variance.