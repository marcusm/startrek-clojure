(ns startrek.core-test
  (:require [midje.sweet :refer :all])
  (:require [startrek.core :as c])
  (:require [startrek.world :as w])
  (:require [startrek.klingon :as k]))

(facts "Check we can end the game in an expected way."
  (tabular
       (fact "Verify the game ends with klingons in the sector."
             (let [ game-state (atom
                   {:enterprise {:is_docked false
                                 :shields shields?
                                 :energy energy?
                                 :sector [6 7]
                                 :quadrant [2 3]}
                    :quads {}
                    :current-sector {}
                    :current-klingons [{:x 2 :y 8 :energy 200} {:x 3 :y 8 :energy 0} {:x 3 :y 8 :energy 200}]
                    :starting-klingons 17
                    :stardate {:start 3200 :current 3220 :end 30}})]
             (c/game-over? game-state) => result?
             (provided
               ; (println anything) => :default :times (range)
               (w/remaining-klingon-count {}) => 17 :times (range))))
       shields? energy? result?
       0 0 truthy
       -1.0 0 truthy
       1 0 truthy
       1 1 falsey)
  (tabular
       (fact "Verify the game ends without klingons in the sector."
             (let [game-state (atom
                   {:enterprise {:is_docked false
                                 :shields shields?
                                 :energy energy?
                                 :sector [6 7]
                                 :quadrant [2 3]}
                    :quads {}
                    :current-sector {}
                    :current-klingons []
                    :starting-klingons 17
                    :stardate {:start 3200 :current 3220 :end 30}})]
             (c/game-over? game-state) => result?
             (provided
               (w/remaining-klingon-count {}) => 17 :times (range))))
       shields? energy? result?
       0 0 truthy
       -1.0 0 truthy
       1 0 truthy
       1 1 falsey))
