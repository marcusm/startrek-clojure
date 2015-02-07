(ns startrek.world-test
  (:use midje.sweet)
  (:require [startrek.world :as w]))

;; These tests are mostly useless, just helping me learn midje.
(facts "We need to have klingons or the game is boring!"
  (fact "Sometimes there are no klingons in a quadrant"
      (#'w/assign-quadrant-klingons) => 0
        (provided
          (w/gen-double) => 0.5))
  (fact "Occasionally there is one klingon in a quadrant."
      (#'w/assign-quadrant-klingons) => 1
        (provided
          (w/gen-double) => 0.85))
  (fact "Rarely there are two klingons in a quadrant."
      (#'w/assign-quadrant-klingons) => 2
        (provided
          (w/gen-double) => 0.97111))
  (fact "Oh no!! There are three klingons!"
      (#'w/assign-quadrant-klingons) => 3
        (provided
          (w/gen-double) => 0.995)))

(facts "We need some bases or the game will be too hard."
  (fact "Usually, a sector has no base."
      (#'w/assign-quadrant-starbases) => 0
        (provided
          (w/gen-double) => 0.5))
  (fact "Rarely, there is a starbase we can use."
      (#'w/assign-quadrant-starbases) => 1
        (provided
          (w/gen-double) => 0.97)))


