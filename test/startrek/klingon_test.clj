(ns startrek.world-test
  (:use midje.sweet)
  (:require [startrek.utils :as u :refer :all])
  (:require [startrek.klingon :as k]))

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
               (k/klingon-turn enterprise current-klingons) => (just {:energy 1000
                                                                      :is_docked false
                                                                      :quadrant {:x 2 :y 3}
                                                                      :sector {:x 6 :y 7}
                                                                      :shields pos?})
               (provided
                 (gen-double) =streams=> [0.55 0.25]))))


(facts "Verify we can detect if a klingon is alive or dead"
       (fact "Living klingon."
             (k/klingon-dead? {:x 3 :y 2 :energy 100}) => falsey)
       (fact "Dead klingon."
             (k/klingon-dead? {:x 3 :y 2 :energy 0}) => truthy))

(facts "Verify klingons can generate a shot result."
       (fact "A fresh enterprise will be fine."
             (let [klingon {:x 2 :y 8 :energy 200}
                   enterprise {:is_docked false
                               :shields 100
                               :energy 1000
                               :sector {:x 6 :y 7}
                               :quadrant {:x 2 :y 3}}]
               (k/klingon-shot enterprise klingon) => (just {:hit pos? :enterprise map?})
               (provided
                 (gen-double) =streams=> [0.55 0.25]))))
