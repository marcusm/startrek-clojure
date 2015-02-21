(ns startrek.enterprise-test
  (:use midje.sweet)
  (:require [startrek.random :as r])
  (:require [startrek.nav :as n])
  (:require [startrek.utils :as u])
  (:require [startrek.world :as w])
  (:require [startrek.enterprise :as e]))

(def game-state-e {:current-klingons  [{:x 4 :y 3 :energy 60} {:x 6 :y 3 :energy 100}]
    :current-sector  [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 3 0 3 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0] 
    :enterprise 
    {
      :damage
         {:computer_display 0 
          :damage_control 0 
          :long_range_sensors 0 
          :phasers 0 
          :photon_torpedo_tubes 0 
          :shields 0 
          :short_range_sensors 0 
          :warp_engines 0
         } 
      :energy 2000 
      :is_docked false
      :photon_torperdoes 10
      :quadrant  [4 1]
      :sector  [1 1]
      :shields 500    
    }
    :quads  [{:bases 0 :klingons 0 :stars 3 :x 1 :y 1}
             {:bases 0 :klingons 0 :stars 8 :x 2 :y 1}
             {:bases 0 :klingons 0 :stars 4 :x 3 :y 1}
             {:bases 0 :klingons 2 :stars 0 :x 4 :y 1}
             {:bases 0 :klingons 0 :stars 2 :x 5 :y 1}
             {:bases 0 :klingons 0 :stars 7 :x 6 :y 1}
             {:bases 0 :klingons 0 :stars 7 :x 7 :y 1}
             {:bases 0 :klingons 0 :stars 7 :x 8 :y 1}
             {:bases 0 :klingons 0 :stars 7 :x 1 :y 2}
             {:bases 0 :klingons 1 :stars 6 :x 2 :y 2}
             {:bases 0 :klingons 0 :stars 8 :x 3 :y 2}
             {:bases 0 :klingons 0 :stars 3 :x 4 :y 2}
             {:bases 0 :klingons 0 :stars 4 :x 5 :y 2}
             {:bases 0 :klingons 0 :stars 7 :x 6 :y 2}
             {:bases 0 :klingons 1 :stars 4 :x 7 :y 2}
             {:bases 0 :klingons 0 :stars 2 :x 8 :y 2}
             {:bases 0 :klingons 0 :stars 4 :x 1 :y 3}
             {:bases 0 :klingons 0 :stars 7 :x 3 :y 3}
             {:bases 0 :klingons 0 :stars 3 :x 2 :y 3}
             {:bases 0 :klingons 0 :stars 3 :x 4 :y 3}
             {:bases 0 :klingons 0 :stars 0 :x 5 :y 3}
             {:bases 0 :klingons 1 :stars 4 :x 6 :y 3}
             {:bases 0 :klingons 0 :stars 6 :x 7 :y 3}
             {:bases 0 :klingons 0 :stars 1 :x 8 :y 3}
             {:bases 0 :klingons 0 :stars 6 :x 1 :y 4}
             {:bases 0 :klingons 1 :stars 4 :x 2 :y 4}
             {:bases 0 :klingons 0 :stars 8 :x 3 :y 4}
             {:bases 0 :klingons 2 :stars 1 :x 4 :y 4}
             {:bases 0 :klingons 0 :stars 8 :x 5 :y 4}
             {:bases 0 :klingons 0 :stars 3 :x 6 :y 4}
             {:bases 0 :klingons 0 :stars 1 :x 7 :y 4}
             {:bases 0 :klingons 0 :stars 7 :x 8 :y 4}
             {:bases 0 :klingons 0 :stars 1 :x 1 :y 5}
             {:bases 0 :klingons 0 :stars 8 :x 2 :y 5}
             {:bases 0 :klingons 0 :stars 1 :x 3 :y 5}
             {:bases 0 :klingons 0 :stars 7 :x 4 :y 5}
             {:bases 0 :klingons 0 :stars 5 :x 5 :y 5}
             {:bases 0 :klingons 0 :stars 8 :x 6 :y 5}
             {:bases 0 :klingons 0 :stars 8 :x 7 :y 5}
             {:bases 0 :klingons 0 :stars 8 :x 8 :y 5}
             {:bases 0 :klingons 0 :stars 3 :x 1 :y 6}
             {:bases 0 :klingons 0 :stars 5 :x 2 :y 6}
             {:bases 0 :klingons 0 :stars 6 :x 3 :y 6}
             {:bases 0 :klingons 0 :stars 7 :x 4 :y 6}
             {:bases 0 :klingons 0 :stars 7 :x 5 :y 6}
             {:bases 0 :klingons 1 :stars 6 :x 6 :y 6}
             {:bases 0 :klingons 1 :stars 6 :x 7 :y 6}
             {:bases 1 :klingons 1 :stars 7 :x 8 :y 6}
             {:bases 0 :klingons 0 :stars 5 :x 1 :y 7}
             {:bases 0 :klingons 0 :stars 5 :x 2 :y 7}
             {:bases 0 :klingons 0 :stars 6 :x 3 :y 7}
             {:bases 0 :klingons 0 :stars 1 :x 4 :y 7}
             {:bases 0 :klingons 0 :stars 8 :x 5 :y 7}
             {:bases 0 :klingons 1 :stars 3 :x 6 :y 7}
             {:bases 0 :klingons 0 :stars 5 :x 7 :y 7}
             {:bases 0 :klingons 0 :stars 5 :x 8 :y 7}
             {:bases 0 :klingons 0 :stars 6 :x 1 :y 8}
             {:bases 0 :klingons 0 :stars 3 :x 2 :y 8}
             {:bases 1 :klingons 0 :stars 3 :x 3 :y 8}
             {:bases 0 :klingons 0 :stars 1 :x 4 :y 8}
             {:bases 0 :klingons 0 :stars 5 :x 5 :y 8}
             {:bases 0 :klingons 0 :stars 7 :x 6 :y 8}
             {:bases 0 :klingons 0 :stars 6 :x 7 :y 8}
             {:bases 0 :klingons 0 :stars 8 :x 8 :y 8}]
 :stardate  {:current 3500 :end 30 :start 3500}
 :starting-klingons 12 }
)
  
(facts "What happens when we first phasers?"
  (fact "We can destroy a klingon."
      (e/fire-phasers-command (atom game-state-e)) => 5
        (provided
          (e/pick-phaser-power) => 500
          (r/gen-double) => 0.5))
)
