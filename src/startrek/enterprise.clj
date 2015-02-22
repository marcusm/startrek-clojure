 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fire Phasers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns startrek.enterprise
   (:require [startrek.random :as r])
   (:require [startrek.utils :as u :refer :all])
   (:require [startrek.klingon :as k]))
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
   :quadrant [(gen-idx) (gen-idx)]
   :sector [(gen-idx) (gen-idx)]
   })
(defn pick-phaser-power [] 200)

(defn ask-phaser-power [energy pick-power]
      (u/message (format "PHASERS LOCKED ON TARGET.  ENERGY AVAILABLE = %s" energy))
      (u/message "NUMBER OF UNITS TO FIRE ");
      (pick-power))

(defn select-phaser-power [energy]
  (loop [power (ask-phaser-power energy pick-phaser-power)]
    (if (or (neg? power) (pos? (- energy power)))
      power
      (recur (ask-phaser-power energy pick-phaser-power)))))

(defn destroy-klingon [game-state klingon]
  (let [p [(:x klingon) (:y klingon)]]
    (u/message (format "*** KLINGON AT SECTOR %s DESTROYED ***" u/point-2-str p))
    (swap! game-state assoc-in [:current-sector (u/coord-to-index p)] 0)

    (swap! game-state update-in [:quads 
                                 (u/coord-to-index (get-in @game-state [:enterprise :quadrant]))
                                 :klingons] - 1))
  (swap! game-state update-in [:starting-klingons] dec)
  nil)

(defn enterprise-attack [game-state power k-count klingon]
  (let [p [(:x klingon) (:y klingon)]
        h (-> @power
              (/ k-count
                 (u/euclidean-distance
                   (get-in @game-state [:enterprise :sector])
                   p))
              (* 2 (r/gen-double)))
        z (max 0.0 (- (:energy klingon) h))]
    
    (u/message (format "%f UNIT HIT ON KLINGON AT SECTOR %s\n   (%f LEEFT)"
                     h
                     (u/point-2-str p)
                     z))
    (assoc klingon :energy z)))


(defn fire-phasers [game-state]
  (when (pos? (get-in @game-state [:enterprise :damage :computer_display]))
    (u/message " COMPUTER FAILURE HAMPERS ACCURACY"))
  (def power (atom (select-phaser-power (get-in @game-state [:enterprise :energy]))))
  (swap! game-state update-in [:enterprise :energy] - @power)
  (swap! game-state assoc-in [:enterprise] (k/klingon-turn 
                                        (get-in @game-state [:enterprise])
                                        (get-in @game-state [:current-klingons])))

  (when-not (neg? (get-in @game-state [:enterprise :shields]))
    (when (neg? (get-in @game-state [:enterprise :damage :computer_display]))
      (swap! power update * (r/gen-double)))

    (def k-count (count (get-in @game-state [:current-klingons])))
    
    (swap! game-state assoc-in [:current-klingons] 
           (->> (get-in @game-state [:current-klingons])
                (map #(enterprise-attack game-state power k-count %))
                (map #(if (pos? (:energy %)) % (destroy-klingon game-state %)))
                (reduce conj)
                (vec)))))

(defn fire-phasers-command [game-state]
  (cond
    (empty? (get-in @game-state [:current-klingons])) (u/message "SHORT RANGE SENSORS REPORT NO KLINGONS IN THIS QUADRANT")
    (neg? (get-in @game-state [:enterprise :damage :phasers])) (u/message "PHASER CONTROL IS DISABLED")
    :else (fire-phasers game-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions to control the enterprise's shields
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-shield-power [] 200)

(defn ask-shield-power [energy shields pick-power]
  (u/message (format "ENERGY AVAILABLE = %d" (int (+ energy shields))) 
             (u/message "NUMBER OF UNITS TO SHIELDS "))
  (pick-power))

(defn select-shield-power [energy shields]
  (loop [power (ask-shield-power energy shields pick-shield-power)]
    (if (or (neg? power) (pos? (- (+ shields energy) power)))
      power
      (recur (ask-shield-power energy shields pick-shield-power))
      )))

(defn set-shield-power [game-state]
  (def power (select-shield-power (get-in @game-state [:enterprise :energy])
                                  (get-in @game-state [:enterprise :shields])))
  (when (pos? power)
    (swap! game-state update-in [:enterprise :energy] +
           (- (get-in @game-state [:enterprise :shields])
              power))
    (swap! game-state assoc-in [:enterprise :shields] power))
  game-state)

(defn shield-control-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :shields]))
      (u/message "SHIELD CONTROL IS NON-OPERATIONAL")
      (set-shield-power game-state)))

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
              (u/message "DAMAGE CONTROL REPORT: %s STATE OF REPAIR IMPROVED" (system damage-station-map)))
            (do
              (update-in @game-state [:enterprise :damage system] - (gen-uniform 1 5))
              (u/message "DAMAGE CONTROL REPORT: %s DAMAGED" (system damage-station-map)))))))))

