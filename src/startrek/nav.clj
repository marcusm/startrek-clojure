(ns startrek.nav
   (:require [startrek.utils :as u])
   (:require [startrek.enterprise :as e])
   (:require [startrek.klingon :as k])
   (:require [clojure.math.numeric-tower :as math]))

(declare get-course get-warp-factor pick-course pick-warp-factor)

(def course-vector
  [[]
   [1 0]   ; dir 1: right
   [1 -1]  ; dir 2: right and up
   [0 -1]  ; dir 3: up
   [-1 -1] ; dir 4: left and up
   [-1 0]  ; dir 5: left
   [-1 1]  ; dir 6: left and down
   [0 -1]  ; dir 7: down
   [1 1]   ; dir 8: right and down
   [1 0]]) ; dir 9: same as dir 1

(defn get-course [] 5)
(defn get-warp-factor [] 5)

(defn pick-course
  "Pick a course that is from 1-9 inclusive. Note: 1 and 9 are the same direction."
  []
  (loop []
    (let [course (get-course)]
      (cond
        (< course 1) (recur)
        (> course 9) (recur)
        :else course))))

(defn- select-warp-factor
  []
  (loop []
    (let [factor (get-warp-factor)]
      (cond
        (neg? factor) (recur)
        (> factor 9) (recur)
        :else factor))))

(defn pick-warp-factor
  [enterprise]
  (loop []
    (let [factor (select-warp-factor)]
      (cond
        (and (pos? (get-in enterprise [:damage :warp_engines]))
             (> factor 0.2))
        (do (println "WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP .2") (recur))
        :else factor))))

(defn- leave-sector? [coord]
  (some true? (concat (map #(< % 0.5) coord)
                      (map #(>= % 8.5) coord))))

(defn- hit-item? [sector coord]
  (let [p (map #(math/round %) coord)]
    (pos? (get sector (u/coord-to-index p)))))

(defn- set-sector-position [game-state factor coord]
  (println (get-in @game-state [:enterprise]))
  (let [e {:sector coord
           :energy (- (get-in @game-state [:enterprise :energy])
                      (+ -5 (* 8 (int factor))))}]
  (swap! game-state update-in [:enterprise] merge e))
  (when (> factor 1)
    (swap! game-state update-in [:stardate :start] + 1)))

(defn- bad-nav [game-state factor coord inc-coord]
  (let [p (map math/round (map - coord inc-coord))]
    (println "WARP ENGINES SHUTDOWN AT SECTOR " (seq coord) " DUE TO BAD NAVIGATION")
    (set-sector-position game-state factor coord)))

(defn warp-travel-distance [enterprise factor inc-coord]
  (map + (map #(* 8 %) (get-in enterprise [:quadrant]))
         (get-in enterprise [:sector])
         (map #(* (* 8 (int factor)) %) inc-coord)))

(defn- leave-sector [game-state factor coord inc-coord]
  (let [place (warp-travel-distance (get-in @game-state [:enterprise])
                                    factor
                                    inc-coord)]
    (def new-quad (map #(int (/ 8 %)) place))
    (def new-sector (map #(math/round %) (- place
                                            (map #(* 8 %) new-quad))))
    ))

(defn move [game-state course factor]
  (let [n (int (* factor 8))
        coord (get-in @game-state [:enterprise :coord])
        sector (get-in @game-state [:current-sector])
        course' (int course)]
    ; clear current enterprise coord
    (aset sector (u/coord-to-index coord))
    (let [inc-cord (map + (course-vector course')
                        (map (partial * (- course course'))
                             (map - (course-vector (+ course' 1)) (course-vector course'))))]
      (loop [p (+ coord inc-cord) i n]
        (cond
          (leave-sector? p) (leave-sector )
          (hit-item? p) (bad-nav game-state factor p inc-cord)
          (zero? i) (set-sector-position game-state factor p)
          :else (recur p (dec i))))
      )))

(defn set-course [game-state]
  (let [course (pick-course) factor (pick-warp-factor (:enterprise @game-state))]
    (assoc-in @game-state [:enterprise] (k/klingon-turn (:enterprise @game-state) (:current-klingons @game-state)))
    (when-not (neg? (get-in @game-state [:enterprise :shields]))
      (e/enterprise-update game-state)
      (move game-state course factor))))

