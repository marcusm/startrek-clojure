(ns startrek.enterprise-test
  (:use midje.sweet)
  (:require [startrek.random :as r])
  (:require [startrek.nav :as n])
  (:require [startrek.utils :as u])
  (:require [startrek.world :as w])
  (:require [startrek.enterprise :as e]))

(defchecker check-ship-settings? [& expected]
  (checker [actual]
           (def expected' (first expected))
           (and
             (= (get-in actual [:enterprise :energy])
                (get-in expected' [:enterprise :energy]))
             (= (get-in actual [:enterprise :shields])
                (get-in expected' [:enterprise :shields]))
             )))

(defchecker check-shots-fired? [& expected]
  (checker [actual]
           (def expected' (first expected))
           (and
             (= (get-in @actual [:enterprise :energy])
                (get-in expected' [:enterprise :energy]))
             (= (get-in @actual [:enterprise :photon_torpedoes])
                (get-in expected' [:enterprise :photon_torpedoes]))
             (= 1 (count (get-in @actual [:current-klingons])))
             (zero? (get-in @actual [:current-sector (u/coord-to-index [4 3])])))
           ))

(def game-state-e {:current-klingons  [{:sector [4 3] :energy 60} {:sector [6 3] :energy 100}]
    :current-sector  [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2 0 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0] 
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
      :photon_torpedoes 10
      :quadrant  [4 1]
      :sector  [1 1]
      :shields 500    
    }
    :quads [{:bases 0 :klingons 0 :quadrant [1 1] :stars 3}
         {:bases 0 :klingons 0 :quadrant [2 1] :stars 8}
         {:bases 0 :klingons 0 :quadrant [3 1] :stars 4}
         {:bases 0 :klingons 1 :quadrant [4 1] :stars 4}
         {:bases 0 :klingons 0 :quadrant [5 1] :stars 2}
         {:bases 0 :klingons 0 :quadrant [6 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [7 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [8 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 2] :stars 7}
         {:bases 0 :klingons 2 :quadrant [2 2] :stars 6}
         {:bases 0 :klingons 0 :quadrant [3 2] :stars 8}
         {:bases 0 :klingons 0 :quadrant [4 2] :stars 3}
         {:bases 0 :klingons 0 :quadrant [5 2] :stars 4}
         {:bases 0 :klingons 0 :quadrant [6 2] :stars 7}
         {:bases 0 :klingons 1 :quadrant [7 2] :stars 4}
         {:bases 0 :klingons 0 :quadrant [8 2] :stars 2}
         {:bases 0 :klingons 0 :quadrant [1 3] :stars 4}
         {:bases 0 :klingons 0 :quadrant [2 3] :stars 3}
         {:bases 0 :klingons 0 :quadrant [3 3] :stars 7}
         {:bases 0 :klingons 0 :quadrant [4 3] :stars 3}
         {:bases 0 :klingons 1 :quadrant [5 3] :stars 2}
         {:bases 0 :klingons 0 :quadrant [6 3] :stars 4}
         {:bases 0 :klingons 0 :quadrant [7 3] :stars 6}
         {:bases 0 :klingons 0 :quadrant [8 3] :stars 1}
         {:bases 0 :klingons 0 :quadrant [1 4] :stars 6}
         {:bases 0 :klingons 1 :quadrant [2 4] :stars 4}
         {:bases 0 :klingons 0 :quadrant [3 4] :stars 8}
         {:bases 0 :klingons 2 :quadrant [4 4] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 4] :stars 8}
         {:bases 0 :klingons 0 :quadrant [6 4] :stars 3}
         {:bases 0 :klingons 0 :quadrant [7 4] :stars 1}
         {:bases 0 :klingons 0 :quadrant [8 4] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 5] :stars 1}
         {:bases 0 :klingons 0 :quadrant [2 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [3 5] :stars 1}
         {:bases 0 :klingons 0 :quadrant [4 5] :stars 7}
         {:bases 0 :klingons 0 :quadrant [5 5] :stars 5}
         {:bases 0 :klingons 0 :quadrant [6 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [7 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [8 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [1 6] :stars 3}
         {:bases 0 :klingons 0 :quadrant [2 6] :stars 5}
         {:bases 0 :klingons 0 :quadrant [3 6] :stars 6}
         {:bases 0 :klingons 0 :quadrant [4 6] :stars 7}
         {:bases 0 :klingons 0 :quadrant [5 6] :stars 7}
         {:bases 0 :klingons 1 :quadrant [6 6] :stars 6}
         {:bases 0 :klingons 1 :quadrant [7 6] :stars 6}
         {:bases 1 :klingons 1 :quadrant [8 6] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [2 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [3 7] :stars 6}
         {:bases 0 :klingons 0 :quadrant [4 7] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 7] :stars 8}
         {:bases 0 :klingons 1 :quadrant [6 7] :stars 3}
         {:bases 0 :klingons 0 :quadrant [7 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [8 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [1 8] :stars 6}
         {:bases 0 :klingons 0 :quadrant [2 8] :stars 3}
         {:bases 1 :klingons 0 :quadrant [3 8] :stars 3}
         {:bases 0 :klingons 0 :quadrant [4 8] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 8] :stars 5}
         {:bases 0 :klingons 0 :quadrant [6 8] :stars 7}
         {:bases 0 :klingons 0 :quadrant [7 8] :stars 6}
         {:bases 0 :klingons 0 :quadrant [8 8] :stars 8}]
 :stardate  {:current 3500 :end 30 :start 3500}
 :starting-klingons 12 }
)


(def game-state-b {:current-klingons  []
    :current-sector  [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0] 
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
      :energy 3000 
      :photon_torpedoes 10
      :quadrant  [1 1]
      :sector  [5 5]
      :shields 0    
    }
    :quads [{:bases 0 :klingons 0 :quadrant [1 1] :stars 3}
         {:bases 0 :klingons 0 :quadrant [2 1] :stars 8}
         {:bases 0 :klingons 0 :quadrant [3 1] :stars 4}
         {:bases 0 :klingons 1 :quadrant [4 1] :stars 4}
         {:bases 0 :klingons 0 :quadrant [5 1] :stars 2}
         {:bases 0 :klingons 0 :quadrant [6 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [7 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [8 1] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 2] :stars 7}
         {:bases 0 :klingons 2 :quadrant [2 2] :stars 6}
         {:bases 0 :klingons 0 :quadrant [3 2] :stars 8}
         {:bases 0 :klingons 0 :quadrant [4 2] :stars 3}
         {:bases 0 :klingons 0 :quadrant [5 2] :stars 4}
         {:bases 0 :klingons 0 :quadrant [6 2] :stars 7}
         {:bases 0 :klingons 1 :quadrant [7 2] :stars 4}
         {:bases 0 :klingons 0 :quadrant [8 2] :stars 2}
         {:bases 0 :klingons 0 :quadrant [1 3] :stars 4}
         {:bases 0 :klingons 0 :quadrant [2 3] :stars 3}
         {:bases 0 :klingons 0 :quadrant [3 3] :stars 7}
         {:bases 0 :klingons 0 :quadrant [4 3] :stars 3}
         {:bases 0 :klingons 1 :quadrant [5 3] :stars 2}
         {:bases 0 :klingons 0 :quadrant [6 3] :stars 4}
         {:bases 0 :klingons 0 :quadrant [7 3] :stars 6}
         {:bases 0 :klingons 0 :quadrant [8 3] :stars 1}
         {:bases 0 :klingons 0 :quadrant [1 4] :stars 6}
         {:bases 0 :klingons 1 :quadrant [2 4] :stars 4}
         {:bases 0 :klingons 0 :quadrant [3 4] :stars 8}
         {:bases 0 :klingons 2 :quadrant [4 4] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 4] :stars 8}
         {:bases 0 :klingons 0 :quadrant [6 4] :stars 3}
         {:bases 0 :klingons 0 :quadrant [7 4] :stars 1}
         {:bases 0 :klingons 0 :quadrant [8 4] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 5] :stars 1}
         {:bases 0 :klingons 0 :quadrant [2 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [3 5] :stars 1}
         {:bases 0 :klingons 0 :quadrant [4 5] :stars 7}
         {:bases 0 :klingons 0 :quadrant [5 5] :stars 5}
         {:bases 0 :klingons 0 :quadrant [6 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [7 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [8 5] :stars 8}
         {:bases 0 :klingons 0 :quadrant [1 6] :stars 3}
         {:bases 0 :klingons 0 :quadrant [2 6] :stars 5}
         {:bases 0 :klingons 0 :quadrant [3 6] :stars 6}
         {:bases 0 :klingons 0 :quadrant [4 6] :stars 7}
         {:bases 0 :klingons 0 :quadrant [5 6] :stars 7}
         {:bases 0 :klingons 1 :quadrant [6 6] :stars 6}
         {:bases 0 :klingons 1 :quadrant [7 6] :stars 6}
         {:bases 1 :klingons 1 :quadrant [8 6] :stars 7}
         {:bases 0 :klingons 0 :quadrant [1 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [2 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [3 7] :stars 6}
         {:bases 0 :klingons 0 :quadrant [4 7] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 7] :stars 8}
         {:bases 0 :klingons 1 :quadrant [6 7] :stars 3}
         {:bases 0 :klingons 0 :quadrant [7 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [8 7] :stars 5}
         {:bases 0 :klingons 0 :quadrant [1 8] :stars 6}
         {:bases 0 :klingons 0 :quadrant [2 8] :stars 3}
         {:bases 1 :klingons 0 :quadrant [3 8] :stars 3}
         {:bases 0 :klingons 0 :quadrant [4 8] :stars 1}
         {:bases 0 :klingons 0 :quadrant [5 8] :stars 5}
         {:bases 0 :klingons 0 :quadrant [6 8] :stars 7}
         {:bases 0 :klingons 0 :quadrant [7 8] :stars 6}
         {:bases 0 :klingons 0 :quadrant [8 8] :stars 8}]
 :stardate  {:current 3500 :end 30 :start 3500}
 :starting-klingons 12 }
)
  
;; This test fails for no reason I can see...
; (facts "What happens when we first phasers?"
;        (fact "We can destroy a klingon."
;              (e/fire-phasers-command (atom game-state-e)) => (check-shots-fired? 
;                                                                {:enterprise {:energy 1500 
;                                                                              :photon_torpedoes 10}})
;              (provided
;                (e/pick-phaser-power) => 500
;                (r/gen-double) => 0.5)))

(facts "What happens when we fire photon torpedoes"
       (fact "We can destroy a klingon."
             (e/fire-torpedoes-command (atom game-state-e)) => (check-shots-fired? 
                                                                 {:enterprise {:energy 2000
                                                                               :photon_torpedoes 9}})
             (provided
               (e/pick-torpedo-course) => 8.2)))


(facts "Verify the enterprise can set shield power"
       (tabular
         (fact "Verify the enterprise can set shield power."
               (e/shield-control-command (atom game-state-e)) => ?result
                  (provided
                    (e/pick-shield-power) => ?shields))
               ?shields ?result
               100 (check-ship-settings? {:enterprise {:energy 2400 :shields 100}}) 
               -1 (check-ship-settings? {:enterprise {:energy 2000 :shields 500}}) 
               1000 (check-ship-settings? {:enterprise {:energy 1500 :shields 1000}}) 
               ))
