(ns startrek.nav-test
  (:use midje.sweet)
  (:require [startrek.nav :as n])
  (:require [startrek.utils :as u])
  (:require [startrek.enterprise :as e])
  (:require [startrek.world :as w]))

(defchecker contains-move? [& expected]
  (checker [actual]
           (def expected' (first expected))
           (every? true? (flatten (conj
                           (map = (get-in actual [:enterprise :sector])
                                (get-in expected' [:enterprise :sector]))
                           (map = (get-in actual [:enterprise :quadrant])
                                (get-in expected' [:enterprise :quadrant]))
                           (= 1 (get-in actual [:current-sector (u/coord-to-index (get-in actual [:enterprise :sector]))]))
                           )))))

(def game-state-a {:current-klingons  []
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
      :is_docked false
      :photon_torpedoes 10
      :quadrant  [5 3]
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
      :is_docked false
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

(facts "Verify the enterprise will move in predictible ways."
       (tabular
         (fact "Verify ship will move exactly 1 sector in chosen direction."
               (n/move (atom game-state-a) ?course ?factor) => ?result)
               ?course ?factor ?result
               1 0.1 (contains-move? {:enterprise {:sector [6 5] :quadrant [5 3]}})
               2 0.2 (contains-move? {:enterprise {:sector [6 4] :quadrant [5 3]}})
               3 0.1 (contains-move? {:enterprise {:sector [5 4] :quadrant [5 3]}})
               4 0.2 (contains-move? {:enterprise {:sector [4 4] :quadrant [5 3]}})
               5 0.1 (contains-move? {:enterprise {:sector [4 5] :quadrant [5 3]}})
               6 0.2 (contains-move? {:enterprise {:sector [4 6] :quadrant [5 3]}})
               7 0.1 (contains-move? {:enterprise {:sector [5 6] :quadrant [5 3]}})
               8 0.2 (contains-move? {:enterprise {:sector [6 6] :quadrant [5 3]}})
               9 0.1 (contains-move? {:enterprise {:sector [6 5] :quadrant [5 3]}})  )
       (tabular
         (fact "Verify ship will move exactly 1 quadrant in chosen direction."
               (n/move (atom game-state-a) ?course ?factor) => ?result)
               ?course ?factor ?result
               1 1.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [6 3]}})
               2 1.0 (contains-move? {:enterprise {:sector [3 7] :quadrant [6 2]}})
               3 1.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [5 2]}})
               4 1.0 (contains-move? {:enterprise {:sector [7 7] :quadrant [4 2]}})
               5 1.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [4 3]}})
               6 1.0 (contains-move? {:enterprise {:sector [7 3] :quadrant [4 4]}})
               7 1.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [5 4]}})
               8 1.0 (contains-move? {:enterprise {:sector [3 3] :quadrant [6 4]}})
               9 1.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [6 3]}})  
               )
       (tabular
         (fact "Verify ship will move exactly 3 quadrants in chosen direction."
               (n/move (atom game-state-a) ?course ?factor) => ?result)
               ?course ?factor ?result
               1 3.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [8 3]}})
               1 4.0 (contains-move? {:enterprise {:sector [8 5] :quadrant [8 3]}})
               3 3.0 (contains-move? {:enterprise {:sector [5 1] :quadrant [5 1]}})
               5 3.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [2 3]}})
               7 3.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [5 6]}})
               9 3.0 (contains-move? {:enterprise {:sector [5 5] :quadrant [8 3]}})  
               )
       (tabular
         (fact "Verify ship will move exactly 8 quadrants in chosen direction."
               (n/move (atom game-state-b) ?course ?factor) => ?result)
               ?course ?factor ?result
               ; 1 8.0 (contains-move? {:enterprise {:sector [8 5] :quadrant [8 1]}})
               ; 5 8.0 (contains-move? {:enterprise {:sector [1 5] :quadrant [1 1]}})
               ; 7 8.0 (contains-move? {:enterprise {:sector [5 8] :quadrant [1 8]}})
               9 8.0 (contains-move? {:enterprise {:sector [8 5] :quadrant [8 1]}})  
               ))

(facts "We need to verify we only pick valid courses"
  (fact "Verify a valid course"
      (n/select-course) => 5
        (provided
          (n/pick-course) => 5))
  (fact "Verify we do not allow courses < 1"
      (n/select-course) => 1
        (provided
          (n/pick-course) =streams=> [-1 -4 0 1] :times 4))
  (fact "Verify we do not allow courses > 9"
      (n/select-course) => 9
        (provided
          (n/pick-course) =streams=> [100 10 9] :times 3)))

(facts "We need to verify we only pick valid warp factors"
  (fact "Verify a valid warp factor"
      (#'n/select-warp-factor (:enterprise game-state-a)) => 5
        (provided
          (n/pick-warp-factor) => 5))
  (fact "Verify we do not allow warp factors < 0"
      (#'n/select-warp-factor (:enterprise game-state-a))  => 0.2
        (provided
          (n/pick-warp-factor) =streams=> [-1 -4 -0.1 0.2] :times 4))
  (fact "Verify we do not allow warp factors > 9"
      (#'n/select-warp-factor (:enterprise game-state-a)) => 9
        (provided
          (n/pick-warp-factor) =streams=> [100 10 9] :times 3)))

(facts "Picking a warp factor depends on user selection and ship damage."
  (let [enterprise (e/reset-enterprise)]
    (tabular
      (fact "A healthy ship can pick any valid warp factor."
          (n/select-warp-factor enterprise) => ?result
            (provided
              (#'n/pick-warp-factor) => ?factor))
            ?factor ?result
            0       0
            0.1     0.1
            4       4
            8       8))
  (let [enterprise (assoc-in (e/reset-enterprise) [:damage :warp_engines] -1)]
    (fact "A ship with damaged engines moves at a crawl."
        (n/select-warp-factor enterprise) => 0.2
          (provided
            (#'n/pick-warp-factor) =streams=> [5 6 2 1 0.5 0.2]))))

(facts "Detect if the ship will move out of the sector"
       (tabular
         (fact "This ship will leave the sector if a boundary is crossed."
               (u/leave-quadrant? ?coord) => ?result)
               ?coord ?result
               [1 1] falsey
               [5 5] falsey
               [8 1] falsey
               [8 8.5] truthy
               [0.5 1.2] falsey
               [0.49 8.2] truthy))
