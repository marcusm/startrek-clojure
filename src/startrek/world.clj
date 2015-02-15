(ns startrek.world
   (:require [startrek.utils :as u :refer :all])
   (:require [startrek.enterprise :as e :refer :all])
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
  (reduce + (map #(:klingons %) quadrants)))

(defn remaining-starbase-count
  "Returns the number of starbases remaining in the game."
  [quadrants]
  (reduce + (map #(:bases %) quadrants)))

; fetch default game state
(defn new-game-state
  "Create a new world game state. Resets the enterprise and the quadrants."
  [game-state]
  (def stardate (* 100 (+ 20 (gen-uniform 1 21))))
  (def quads (reset-quadrants))

  (reset! game-state
    {:enterprise (reset-enterprise)
    :quads quads
    :current-sector {}
    :current-klingons []
    :starting-klingons (remaining-klingon-count quads)
    :stardate {:start stardate :current stardate :end 30}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; All of these methods are used to fill quadrants with actors.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- reset-quadrants
  "Returns a new quadrants 2d-array that is guaranteed to have at least 1 klingon and 1 starbase."
  []
  (defn create-quadrants []
    (vec (for [y (range dim) x (range dim)]
           {:x x 
            :y y 
            :klingons (assign-quadrant-klingons) 
            :stars (assign-quadrant-stars)
            :bases (assign-quadrant-starbases)})))

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

(defn create-empty-sectors2 []
  (vec (for [y (range dim) x (range dim)] 0)))

(defn init-sector
  "Initialize a sector with the right amount of game related items."
  [game-state]
  (defn create-empty-sector []
    (vec (for [y (range dim) x (range dim)] 0)))

  (def quad (get (:quads @game-state)
                  (coord-to-index (get-in @game-state [:enterprise :quadrant]))))
  (swap! game-state assoc-in [:current-klingons] [{:x 1 :y 1 :energy 200} {:x 1 :y 2 :energy 200}])

  (let [sector (create-empty-sector)]
    (assoc sector
          (coord-to-index (get-in @game-state [:enterprise :sector]))
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
      (if (pos? (get sector (coord-to-index [x y])))
        (recur)
        (assoc sector (coord-to-index [x y]) value) ))))

(defn- assign-sector-klingon
  "Assign an item in a sector to a location."
  [sector value game-state]
  (loop []
    (let [x (gen-idx) y (gen-idx)]
      (if (pos? (get sector (coord-to-index [x y])))
        (recur)
        (do
          (swap! game-state
                 assoc-in [:current-klingons]
                          (conj (get-in @game-state [:current-klingons]) {:x x :y y :energy 200}))
          (assoc sector (coord-to-index [x y]) value))))))

