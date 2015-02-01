(ns startrek.core-test
  (:use midje.sweet)
  (:use [startrek.core]))

(facts "About creating the starmap"
  (fact "I can reliably add some Klingons into a quadrant."
        (get-klingons) => 0))
