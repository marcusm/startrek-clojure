(ns startrek.world
   (:require [clojure.data.generators :as gen]))

;; randomness wrappers
(declare gen-idx gen-idx gen-double gen-uniform)

;; populating quadrants
(declare assign-quadrant-klingons assign-quadrant-starbases assign-quadrant-stars reset-quadrants)

;; populating sectors
(declare assign-sector-item init-sector)

;; global constants
(def dim           9)
;; sector map values
(def enterprise-id 1)
(def klingon-id    2)
(def base-id       3)
(def star-id       4)

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
   })

; fetch default game state
(defn new-game-state
  "Create a new world game state. Resets the enterprise and the quadrants."
  [game-state]
  (defn gen-stardate [] (* 100 (+ 20 (gen-uniform 1 21))))

  (reset! game-state
    {:enterprise (reset-enterprise)
    :quadrant {:x (gen-idx) :y (gen-idx)}
    :sector {:x (gen-idx) :y (gen-idx)}
    :quads (reset-quadrants)
    :current-sector {}
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
(defn init-sector
  "Initialize a sector with the right amount of game related items."
  [game-state]
  (defn create-empty-sector []
    (to-array-2d
      (partition  dim
                 (for  [y  (range dim) x  (range dim)]  0))))
  (def quad (aget (:quads @game-state) (get-in @game-state [:quadrant :x]) (get-in @game-state [:quadrant :y])))

  (let [sector (create-empty-sector)]
    (aset sector (get-in @game-state [:sector :x]) (get-in @game-state [:sector :y]) enterprise-id)
    (loop [k (:klingons quad)]
      (when (pos? k)
        (assign-sector-item sector klingon-id)
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
       (aset sector x y value)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Wraps the random distribution methods so I can swap them out when testing.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn gen-idx [] (gen/uniform 0 dim))

(defn gen-double [] (gen/double))

(defn gen-uniform [a b]  (gen/uniform a b))
