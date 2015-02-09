(ns startrek.world-test
  (:use midje.sweet)
  (:require [startrek.utils :as u :refer :all])
  (:require [startrek.world :as w]))

;; These tests are mostly useless, just helping me learn midje.
(facts "We need to have klingons or the game is boring!"
  (tabular
    (fact "The amount of klingons we get varies base on a random number roll."
        (#'w/assign-quadrant-klingons) => ?result
          (provided
            (gen-double) => ?gen))
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
            (gen-double) => ?gen))
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


(facts "The klingons get to shoot at the enterprise."
       (fact "A fresh enterprise will be fine."
             (let [current-klingons [{:x 2 :y 8 :energy 200}
                                     {:x 3 :y 8 :energy 0}
                                     {:x 3 :y 8 :energy 200}]
                   enterprise {:is_docked false
                               :shields 100
                               :energy 1000
                               :sector {:x 6 :y 7}
                               :quadrant {:x 2 :y 3}}]
               (w/klingon-turn enterprise current-klingons) => (just {:energy 1000
                                                                      :is_docked false
                                                                      :quadrant {:x 2 :y 3}
                                                                      :sector {:x 6 :y 7}
                                                                      :shields pos?})
               (provided
                 (gen-double) =streams=> [0.55 0.25]))))


(facts "Verify we can detect if a klingon is alive or dead"
  (fact "Living klingon."
      (w/klingon-dead? {:x 3 :y 2 :energy 100}) => falsey)
  (fact "Dead klingon."
      (w/klingon-dead? {:x 3 :y 2 :energy 0}) => truthy))

(facts "Verify klingons can generate a shot result."
       (fact "A fresh enterprise will be fine."
             (let [klingon {:x 2 :y 8 :energy 200}
                   enterprise {:is_docked false
                               :shields 100
                               :energy 1000
                               :sector {:x 6 :y 7}
                               :quadrant {:x 2 :y 3}}]
             (w/klingon-shot enterprise klingon) => (just {:hit pos? :enterprise map?})
             (provided
               (gen-double) =streams=> [0.55 0.25]))))

