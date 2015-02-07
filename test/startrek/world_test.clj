(ns startrek.world-test
  (:use midje.sweet)
  (:require [startrek.world :as w]))

;; These tests are mostly useless, just helping me learn midje.
(facts "We need to have klingons or the game is boring!"
  (tabular
    (fact "The amount of klingons we get varies base on a random number roll."
        (#'w/assign-quadrant-klingons) => ?result
          (provided
            (w/gen-double) => ?gen))
          ?gen      ?result
          0.0       0
          0.7       0
          0.801     1
          0.95      1
          0.9501    2
          0.961     2
          0.97111   2
          0.995     3
          1.0       3))

(facts "We need some bases or the game will be too hard."
  (tabular
    (fact "Getting a base depends on a random number roll."
        (#'w/assign-quadrant-starbases) => ?result
          (provided
            (w/gen-double) => ?gen))
          ?gen      ?result
          0.0       0
          0.7       0
          0.801     0
          0.95      0
          0.96      0
          0.961     1
          0.97111   1
          0.995     1
          1.0       1))
