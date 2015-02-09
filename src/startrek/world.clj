(ns startrek.world
   (:require [startrek.utils :as u :refer :all])
   (:require [clojure.math.numeric-tower :as math]))

;; populating quadrants
(declare assign-quadrant-klingons assign-quadrant-starbases assign-quadrant-stars reset-quadrants)

;; populating sectors
(declare assign-sector-item init-sector)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; These methods are used to check on the current world state.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn remaining-klingon-count
  "Returns the number of klingon ships left in the game."
  [quadrants]
  (reduce + (flatten (map #(map :klingons %) quadrants))))

(defn remaining-starbase-count
  "Returns the number of starbases remaining in the game."
  [quadrants]
  (reduce + (flatten (map #(map :bases %) quadrants))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; These methods are used to reset and initialize the world.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn reset-enterprise
  "Returns a new instance of the enterprise in the default (repaired) state."
  []
  {:damage
   {:short_range_sensors 0
    :computer_display 0
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

; fetch default game state
(defn new-game-state
  "Create a new world game state. Resets the enterprise and the quadrants."
  [game-state]
  (defn gen-stardate [] (* 100 (+ 20 (gen-uniform 1 21))))

  (reset! game-state
    {:enterprise (reset-enterprise)
    :quads (reset-quadrants)
    :current-sector {}
    :current-klingons []
    :stardate {:start (gen-stardate) :end 30}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; All of these methods are used to fill quadrants with actors.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- reset-quadrants
  "Returns a new quadrants 2d-array that is guaranteed to have at least 1 klingon and 1 starbase."
  []
  (defn create-quadrants []
    (to-array-2d 
      (partition  dim
                 (for  [y  (range dim) x  (range dim)]
                   {:x x :y y :klingons (assign-quadrant-klingons) :stars (assign-quadrant-stars) :bases (assign-quadrant-starbases)}))))

  (loop []
  (let [quadrants (create-quadrants)]
    (cond
      (<= (remaining-klingon-count quadrants) 0) (recur)
      (<= (remaining-starbase-count quadrants) 0) (recur)
      :else quadrants))))

(defn- assign-quadrant-klingons
 "Returns 0-3 klingons in a quadrant based on a uniform distribution."
 []
  (let [chance (gen-double)]
    (condp < chance
      0.98 3
      0.95 2
      0.8  1
      0)))

(defn- assign-quadrant-starbases
 "Returns 0-1 starbases in a quadrant based on a uniform distribution."
  []
  (let [chance (gen-double)]
    (condp < chance
      0.96 1
      0)))

(defn- assign-quadrant-stars
  "Returns 1-9 stars for each sector based on a uniform distribution."
  [] 
  (gen-uniform 1 9))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; All of these methods are used to fill quadrants with actors.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare assign-sector-klingon)
(defn init-sector
  "Initialize a sector with the right amount of game related items."
  [game-state]
  (defn create-empty-sector []
    (to-array-2d
      (partition  dim
                 (for  [y  (range dim) x  (range dim)]  0))))
  (def quad (aget (:quads @game-state)
                  (get-in @game-state [:enterprise :quadrant :x])
                  (get-in @game-state [:enterprise :quadrant :y])))
  (swap! game-state assoc-in [:current-klingons] [{:x 1 :y 1 :energy 200} {:x 1 :y 2 :energy 200}])

  (let [sector (create-empty-sector)]
    (aset sector
          (get-in @game-state [:enterprise :sector :x])
          (get-in @game-state [:enterprise :sector :y]) 
          enterprise-id)

    (loop [k (:klingons quad)]
      (when (pos? k)
        (assign-sector-klingon sector klingon-id game-state)
        (recur (dec k))))
    (loop [k (:bases quad)]
      (when (pos? k)
        (assign-sector-item sector base-id)
        (recur (dec k))))
    (loop [k (:stars quad)]
      (when (pos? k)
        (assign-sector-item sector star-id)
        (recur (dec k))))
    (swap! game-state assoc-in [:current-sector] sector)))

(defn- assign-sector-item
  "Assign an item in a sector to a location."
  [sector value]
  (loop []
    (let [x (gen-idx) y (gen-idx)]
      (if (pos? (aget sector x y ))
        (recur)
        (aset sector x y value) ))))

(defn- assign-sector-klingon
  "Assign an item in a sector to a location."
  [sector value game-state]
  (loop []
    (let [x (gen-idx) y (gen-idx)]
      (if (pos? (aget sector x y ))
        (recur)
        (do
          (swap! game-state
                 assoc-in [:current-klingons]
                          (conj (get-in @game-state [:current-klingons]) {:x x :y y :energy 200}))
          (aset sector x y value))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Klingons get to attack too.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn strip-x-y [a-map]
  (let [{:keys [x y]} a-map]
    [x y]))

(defn- klingon-shot-strength
  [klingon enterprise]
  (* (/ (get-in klingon [:energy])
        (euclidean-distance (strip-x-y klingon) (strip-x-y enterprise)))
     2
     (gen-double)))

(defn klingon-dead? [klingon]
  (<= (:energy klingon) 0))

(defn klingon-shot [enterprise klingon]
  (let [h (klingon-shot-strength klingon (-> enterprise :sector))]
    {:hit h :enterprise (update-in enterprise [:shields] - h)}))

(defn klingon-attack [enterprise klingon]
  (if (klingon-dead? klingon)
    enterprise
    (let [r (klingon-shot enterprise klingon)]
      (println (format "%3.1f UNIT HIT ON ENTERPRISE FROM SECTOR %d,%d" (:hit r) (:x klingon) (:y klingon)))
      (println (format "   (%3.1f LEFT)" (max 0 (get-in r [:enterprise :shields]))))
      (:enterprise r))))

(defn klingon-turn [enterprise klingons]
  (reduce klingon-attack enterprise klingons))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The functions repair damage to the enterprise during turns.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- map-function-on-map-vals  [m f]
  (reduce
    (fn  [altered-map  [k v]]
      (assoc altered-map k  (f v)))  {} m))

(defn- repair-system [v]
  (if (pos? v)
    (dec v)
    v))

(defn repair-damage
  "This function handles repairing damage to the enterprise during every turn."
  [enterprise]
  (update-in enterprise [:damage] map-function-on-map-vals repair-system))
  ; (assoc enterprise
  ;        :damage
  ;        (map-function-on-map-vals (:damage enterprise) repair-system)))

