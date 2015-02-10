(ns startrek.enterprise
   (:require [startrek.utils :as u :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; These methods are used to reset and initialize the world.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn reset-enterprise
  "Returns a new instance of the enterprise in the default (repaired) state."
  []
  {:damage
   {:short_range_sensors 0
    :computer_display 0
    :damage_control 0
    :long_range_sensors 0
    :phasers 0
    :warp_engines 0
    :photon_torpedo_tubes 0
    :shields 0}
   :photon_torperdoes 10
   :energy 3000
   :shields 0
   :is_docked false
   :quadrant {:x (gen-idx) :y (gen-idx)}
   :sector {:x (gen-idx) :y (gen-idx)}
   })


   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The functions repair damage to the enterprise during turns.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def damage-station-map
   {:short_range_sensors "S.R. SENSORS"
    :computer_display "COMPUTER    "
    :damage_control "DAMAGE CNTRL"
    :long_range_sensors "L.R. SENSORS"
    :phasers "PHASER CNTRL"
    :warp_engines "WARP ENGINES"
    :photon_torpedo_tubes "PHOTON TUBES"
    :shields "SHIELD CNTRL"})

(defn- map-function-on-map-vals  [m f]
  (reduce
    (fn  [altered-map  [k v]]
      (assoc altered-map k (f v))) {} m))

(defn- repair-system [v]
  (if (pos? v)
    (dec v)
    v))

(defn repair-damage
  "This function handles repairing damage to the enterprise during every turn."
  [enterprise]
  (update-in enterprise [:damage] map-function-on-map-vals repair-system))

(defn get-system [enterprise]
  (-> (:damage enterprise) (keys) (sort) (nth (gen-uniform 0 8))))

(defn enterprise-update [game-state]
  (when-not (empty? (get-in @game-state [:current-klingons]))
    (when (pos? (get-in @game-state [:enterprise :enterprise]))
      (repair-damage (get-in @game-state [:enterprise]))
      (when (<= (gen-double) 0.2)
        (let [system (get-system (:enterprise @game-state))]
          (if (>= (gen-double) 0.5)
            (do
              (update-in @game-state [:enterprise :damage system] + (gen-uniform 1 5))
              (println "DAMAGE CONTROL REPORT: %s STATE OF REPAIR IMPROVED" (system damage-station-map)))
            (do
              (update-in @game-state [:enterprise :damage system] - (gen-uniform 1 5))
              (println "DAMAGE CONTROL REPORT: %s DAMAGED" (system damage-station-map)))))))))

