(ns startrek.nav-test
  (:use midje.sweet)
  (:require [startrek.nav :as n]))

(facts "We need to verify we only pick valid courses"
  (fact "Verify a valid course"
      (n/pick-course) => 5
        (provided
          (n/get-course) => 5))
  (fact "Verify we do not allow courses < 1"
      (n/pick-course) => 1
        (provided
          (n/get-course) =streams=> [-1 -4 0 1] :times 4))
  (fact "Verify we do not allow courses > 9"
      (n/pick-course) => 9
        (provided
          (n/get-course) =streams=> [100 10 9] :times 3)))
