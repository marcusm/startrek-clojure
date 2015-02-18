(ns startrek.nav
   (:require [startrek.utils :as u])
   (:require [startrek.enterprise :as e])
   (:require [startrek.klingon :as k])
   (:require [startrek.world :as w])
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

(defn get-course [] 7)
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
        (do (u/message "WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP .2") (recur))
        :else factor))))

(defn- leave-sector? [coord]
  (some true? (concat (map #(< % 0.5) coord)
                      (map #(>= % 8.5) coord))))

(defn- hit-item? [sector coord]
  (let [p (map #(math/round %) coord)]
    (pos? (get sector (u/coord-to-index p)))))

(defn- set-sector-position [game-state factor coord]
  (let [e {:sector (vec (map #(math/round %) coord))
           :energy (- (get-in @game-state [:enterprise :energy])
                      (+ -5 (* 8 (int factor))))}]
  (swap! game-state update-in [:enterprise] merge e))
  (when (> factor 1)
    (swap! game-state update-in [:stardate :start] + 1)))

(defn- bad-nav [game-state factor coord inc-coord]
  (let [p (map math/round (map - coord inc-coord))]
    (u/message "WARP ENGINES SHUTDOWN AT SECTOR " (seq coord) " DUE TO BAD NAVIGATION")
    (set-sector-position game-state factor p)))

(defn warp-travel-distance [enterprise factor dir-vec]
  ; (println enterprise)
  ; (println factor)
  ; (println dir-vec)

  (vec (map + (map #(* 8 %) (get-in enterprise [:quadrant]))
         (get-in enterprise [:sector])
         (map #(* (* 8 factor) %) dir-vec))))

(defn enter-quadrant [game-state]
  ; ensure good sector/quadrant coords
  (let [sector (->> (get-in @game-state [:enterprise :sector])
                            (map #(max % 1))
                            (map #(min % 8)))
        quadrant (->> (get-in @game-state [:enterprise :quadrant])
                              (map #(max % 1))
                              (map #(min % 8)))]
    (swap! game-state update-in [:enterprise] merge {:sector sector :quadrant quadrant})
    (if (and (pos? (get-in @game-state [:quads (u/coord-to-index quadrant) :klingons]))
             (> (get-in @game-state [:enterprise :shields] 200)))
      (u/message "COMBAT AREA      CONDITION RED")
      (u/message "   SHIELDS DANGEROUSLY LOW"))
    (w/init-sector game-state)))


(defn- leave-quadrant [game-state factor coord dir-vec]
  ; (println "leaving quadrant")
  (let [place (warp-travel-distance (get-in @game-state [:enterprise])
                                    factor
                                    dir-vec)
        energy (- (get-in @game-state [:enterprise :energy])
                  (+ -5 (* 8 (int factor))))]
    (def q (vec (map #(int (/ % 8)) place)))
    (def s (vec (map #(math/round %) (map - place (vec (map #(* 8 %) q))))))

    ; (println "place " place "q " q "s " s)

    (with-local-vars [new-quad q new-sector s]
      (when (zero? (first @new-sector))
        (var-set new-sector (assoc @new-sector 0 8))
        (var-set new-quad (update-in @new-quad [0] dec)))
      (when (zero? (second @new-sector))
        (var-set new-sector (assoc @new-sector 1 8))
        (var-set new-quad (update-in @new-sector [1] dec)))
      (def updates {:sector @new-sector :quadrant @new-quad :energy energy})
      (swap! game-state update-in [:enterprise] merge updates)
      (when (> factor 1)
        (swap! game-state update-in [:stardate :start] inc))))
  (enter-quadrant game-state))

(defn move [game-state course factor]
  (let [n (int (* factor 8))
        coord (get-in @game-state [:enterprise :sector])
        course' (- course 1)]
    ; clear current enterprise coord
    (swap! game-state assoc-in [:current-sector (u/coord-to-index coord)] 0)

    ;; travel direction is based on radian direction
    (let [polar (* Math/PI (/ course' 4))
          dir-vec [(Math/cos polar) (Math/sin polar)]]
      (loop [p (map + coord dir-vec) i n]
        (cond
          (leave-sector? p) (leave-quadrant game-state factor coord dir-vec)
          (hit-item? (get-in @game-state [:current-sector]) p) (bad-nav game-state factor p dir-vec)
          (<= i 0 ) (set-sector-position game-state factor p)
          :else (recur (map + p dir-vec) (dec i))))

      (swap! game-state assoc-in [:current-sector 
                                  (u/coord-to-index 
                                    (get-in @game-state [:enterprise :sector]))] 
             1)
      (merge @game-state {:quads []})
      )))

(defn set-course [game-state]
  (let [course (pick-course) factor (pick-warp-factor (:enterprise @game-state))]
    (assoc-in @game-state [:enterprise] (k/klingon-turn 
                                          (:enterprise @game-state) 
                                          (:current-klingons @game-state)))
    (when-not (neg? (get-in @game-state [:enterprise :shields]))
      (e/enterprise-update game-state)
      (move game-state course factor))))

