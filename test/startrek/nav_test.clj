(ns startrek.nav-test
  (:use midje.sweet)
  (:require [startrek.nav :as n])
  (:require [startrek.enterprise :as e])
  (:require [startrek.world :as w]))

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

(facts "We need to verify we only pick valid warp factors"
  (fact "Verify a valid warp factor"
      (#'n/select-warp-factor) => 5
        (provided
          (n/get-warp-factor) => 5))
  (fact "Verify we do not allow warp factors < 0"
      (#'n/select-warp-factor) => 0.2
        (provided
          (n/get-warp-factor) =streams=> [-1 -4 -0.1 0.2] :times 4))
  (fact "Verify we do not allow warp factors > 9"
      (#'n/select-warp-factor) => 9
        (provided
          (n/get-warp-factor) =streams=> [100 10 9] :times 3)))

(facts "Picking a warp factor depends on user selection and ship damage."
  (let [enterprise (e/reset-enterprise)]
    (tabular
      (fact "A healthy ship can pick any valid warp factor."
          (n/pick-warp-factor enterprise) => ?result
            (provided
              (#'n/select-warp-factor) => ?factor))
            ?factor ?result
            0       0
            0.1     0.1
            4       4
            8       8))
  (let [enterprise (assoc-in (e/reset-enterprise) [:damage :warp_engines] 1)]
    (fact "A ship with damaged engines moves at a crawl."
        (n/pick-warp-factor enterprise) => 0.2
          (provided
            (#'n/select-warp-factor) =streams=> [5 6 2 1 0.5 0.2]))))

