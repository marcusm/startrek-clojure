(ns startrek.enterprise
   (:require [clojure.math.numeric-tower :as math])
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
   :photon_torpedoes 10
   :energy 3000
   :shields 0
   :is_docked false
   :quadrant [(gen-idx) (gen-idx)]
   :sector [(gen-idx) (gen-idx)]
   })
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Publicly exported functions for use by core.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare fire-phasers 
         fire-torpedoes 
         shield-control 
         damage-control-report 
         repair-damage 
         get-system
         damage-station-map)

(defn fire-phasers-command [game-state]
  (cond
    (empty? (get-in @game-state [:current-klingons])) (u/message "SHORT RANGE SENSORS REPORT NO KLINGONS IN THIS QUADRANT")
    (neg? (get-in @game-state [:enterprise :damage :phasers])) (u/message "PHASER CONTROL IS DISABLED")
    :else (fire-phasers game-state)))

(defn fire-torpedoes-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :photon_torpedo_tubes]))
    (u/message "PHOTON TUBES ARE NOT OPERATIONAL")
    (if (pos? (get-in @game-state [:enterprise :photon_torpedoes]))
      (fire-torpedoes game-state)
      (u/message "ALL PHOTON TORPEDOES EXPENDED"))))

(defn shield-control-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :shields]))
      (u/message "SHIELD CONTROL IS NON-OPERATIONAL")
      (shield-control game-state)))

(defn damage-control-report-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :damage_control]))
    (u/message "DAMAGE CONTROL REPORT IS NOT AVAILABLE")
    (damage-control-report game-state)))

(defn enterprise-update [game-state]
  (when (empty? (get-in @game-state [:current-klingons]))
    (repair-damage (get-in @game-state [:enterprise]))
    (when (<= (gen-double) 0.2)
      (let [system (get-system (:enterprise @game-state))]
        (if (>= (gen-double) 0.5)
          (do
            (update-in @game-state [:enterprise :damage system] + (gen-uniform 1 5))
            (u/message (format "DAMAGE CONTROL REPORT: %s STATE OF REPAIR IMPROVED" (system damage-station-map))))
          (do
            (update-in @game-state [:enterprise :damage system] - (gen-uniform 1 5))
            (u/message (format "DAMAGE CONTROL REPORT: %s DAMAGED" (system damage-station-map)))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fire Phasers Command
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-phaser-power [] 200)

(defn- ask-phaser-power [energy pick-power]
      (u/message (format "PHASERS LOCKED ON TARGET.  ENERGY AVAILABLE = %s" energy))
      (u/message "NUMBER OF UNITS TO FIRE ");
      (pick-power))

(defn- select-phaser-power [energy]
  (loop [power (ask-phaser-power energy pick-phaser-power)]
    (if (or (neg? power) (pos? (- energy power)))
      power
      (recur (ask-phaser-power energy pick-phaser-power)))))

(defn- remove-klingon [game-state p]
  (swap! game-state assoc-in [:current-sector (u/coord-to-index p)] 0)

  (swap! game-state update-in [:quads 
                               (u/coord-to-index (get-in @game-state [:enterprise :quadrant]))
                               :klingons] - 1))

(defn- phasers-hit-klingon [game-state klingon]
  (let [p [(:x klingon) (:y klingon)]]
    (u/message (format "*** KLINGON AT SECTOR %s DESTROYED ***" (u/point-2-str p)))
    (remove-klingon game-state p)
  nil))

(defn- enterprise-attack [game-state power k-count klingon]
  (let [p [(:x klingon) (:y klingon)]
        h (-> @power
              (/ k-count
                 (u/euclidean-distance
                   (get-in @game-state [:enterprise :sector])
                   p))
              (* 2 (r/gen-double)))
        z (max 0.0 (- (:energy klingon) h))]
    
    (u/message (format "%f UNIT HIT ON KLINGON AT SECTOR %s\n   (%f LEFT)"
                     h
                     (u/point-2-str p)
                     z))
    (assoc klingon :energy z)))

(defn- fire-phasers [game-state]
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
                (map #(if (pos? (:energy %)) % (phasers-hit-klingon game-state %)))
                (remove nil?)
                (vec)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fire Photon Torpedos
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-torpedo-course [] 1)

(defn- ask-torpedo-course [pick-course]
  (u/message "TORPEDO COURSE (1-9) ")
  (pick-torpedo-course))

(defn- select-torpedo-course []
  (loop [course (ask-torpedo-course pick-torpedo-course)]
    (if (or (zero? course) (and (> course 0) (< course 9)))
      course
      (recur (ask-torpedo-course pick-torpedo-course)))))

(defn- torpedo-hit-klingon [game-state coord]
  (u/message "*** KLINGON DESTROYED ***")
  (swap! game-state assoc-in [:current-klingons] 
         (->> (get-in @game-state [:current-klingons])
              (map #(if (= coord [(:x %) (:y %)]) nil %))
              (remove nil?)
              (vec)))
  (remove-klingon game-state coord))

(defn- torpedo-hit-base [game-state coord]
  (u/message "*** STAR BASE DESTROYED ***  .......CONGRATULATIONS")
  (swap! game-state assoc-in [:current-sector (u/coord-to-index coord)] 0))

(defn- torpedo-hit-star []
  (u/message "YOU CAN'T DESTROY STARS SILLY") 
  (u/message "TORPEDO MISSED"))

(defn- fire-torpedoes [game-state]
  (def course (dec (select-torpedo-course)))
  (swap! game-state update-in [:enterprise :photon_torpedoes] dec)

  (let [polar (* Math/PI (/ course -4))
        dir-vec [(Math/cos polar) (Math/sin polar)]
        coord (get-in @game-state [:enterprise :sector])]

    (u/message "TORPEDO TRACK:")
    (loop [p (map + coord dir-vec)]
      (let [t (map #(math/round %) p) 
            s (get-in @game-state [:current-sector (u/coord-to-index t)])]
        
        (u/message (u/point-2-str t))
        (cond
          (u/leave-quadrant? p) (u/message "TORPEDO MISSED")
          (= s u/klingon-id) (torpedo-hit-klingon game-state t)
          (= s u/base-id) (torpedo-hit-base game-state t)
          (= s u/star-id) (torpedo-hit-star) 
          :else (recur (map + p dir-vec))))))

  (when (pos? (count (get-in @game-state [:current-klingons])))
    (swap! game-state assoc-in [:enterprise] (k/klingon-turn 
                                               (get-in @game-state [:enterprise])
                                               (get-in @game-state [:current-klingons]))))
  game-state)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions to control the enterprise's shields
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-shield-power [] 200)

(defn- ask-shield-power [energy shields pick-power]
  (u/message (format "ENERGY AVAILABLE = %d" (int (+ energy shields))))
  (u/message "NUMBER OF UNITS TO SHIELDS ")
  (pick-power))

(defn- select-shield-power [energy shields]
  (loop [power (ask-shield-power energy shields pick-shield-power)]
    (if (or (neg? power) (pos? (- (+ shields energy) power)))
      power
      (recur (ask-shield-power energy shields pick-shield-power))
      )))

(defn- shield-control [game-state]
  (def power (select-shield-power (get-in @game-state [:enterprise :energy])
                                  (get-in @game-state [:enterprise :shields])))
  (when (pos? power)
    (swap! game-state update-in [:enterprise :energy] +
           (- (get-in @game-state [:enterprise :shields])
              power))
    (swap! game-state assoc-in [:enterprise :shields] power))
  game-state)

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

(defn- repair-damage
  "This function handles repairing damage to the enterprise during every turn."
  [enterprise]
  (update-in enterprise [:damage] map-function-on-map-vals repair-system))

(defn- get-system [enterprise]
  (-> (:damage enterprise) (keys) (sort) (nth (gen-uniform 0 8))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Damage Control Report functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- damage-control-report [game-state]
  (u/message "DEVICE        STATE OF REPAIR")

  (doseq [damage (->> (get-in @game-state [:enterprise :damage])
       (sort)
       (map #(format "%-15s %-2d" ((first %) damage-station-map) (int (second %))))
       )] (u/message damage)))

(defn damage-control-report-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :damage_control]))
    (u/message "DAMAGE CONTROL REPORT IS NOT AVAILABLE")
    (damage-control-report game-state)))
