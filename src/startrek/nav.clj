(ns startrek.nav
   (:require [startrek.utils :as u])
   (:require [startrek.enterprise :as e])
   (:require [startrek.klingon :as k])
   (:require [startrek.world :as w])
   (:require [clojure.math.numeric-tower :as math]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Publicly exported commands from the nav file
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare move select-course select-warp-factor)


(defn set-course-command [game-state]
  (let [course (select-course) factor (select-warp-factor (:enterprise @game-state))]
    (assoc-in @game-state [:enterprise] (k/klingon-turn 
                                          (:enterprise @game-state) 
                                          (:current-klingons @game-state)))
    (when-not (neg? (get-in @game-state [:enterprise :shields]))
      (e/enterprise-update game-state)
      (move game-state course factor))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Course functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-course [] 1)

(defn- ask-course []
  (u/message "COURSE (1-9)")
  (pick-course))

(defn select-course
  "Pick a course that is from 1-9 inclusive. Note: 1 and 9 are the same direction."
  []
  (loop []
    (let [course (ask-course)]
      (cond
        (< course 1) (recur)
        (> course 9) (recur)
        :else course))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Warp-factor functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn pick-warp-factor [] 5)

(defn- ask-warp-factor
  []
  (u/message "WARP FACTOR (0-8) ")
  (pick-warp-factor))

(defn- warp-engines-down? [enterprise factor]
  (and (pos? (get-in enterprise [:damage :warp_engines])) (> factor 0.2)))

(defn select-warp-factor
  [enterprise]
  (loop []
    (let [factor (ask-warp-factor)]
      (cond
        (neg? factor) (recur)
        (> factor 9) (recur)
        (warp-engines-down? enterprise factor) (do 
                                                 (u/message "WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP .2") 
                                                 (recur))
        :else factor))))

(defn- warp-travel-distance [enterprise factor dir-vec]
  (vec (map + (map #(* 8 %) (get-in enterprise [:quadrant]))
         (get-in enterprise [:sector])
         (map #(* (* 8 factor) %) dir-vec))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Navigation resolution functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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

(defn- bad-nav [game-state factor coord dir-vec]
  (let [p (map math/round (map - coord dir-vec))]
    (u/message "WARP ENGINES SHUTDOWN AT SECTOR " (seq coord) " DUE TO BAD NAVIGATION")
    (set-sector-position game-state factor p)))

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
    (w/place-quadrant game-state)))

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
      (def updates {:sector (vec @new-sector) :quadrant (vec @new-quad) :energy energy})
      (swap! game-state update-in [:enterprise] merge updates)
      ; (println "enterprise =>" (get-in @game-state [:enterprise]))
      (when (> factor 1)
        (swap! game-state update-in [:stardate :start] inc))))

  (swap! game-state assoc-in [:current-klingons] [])
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
          (u/leave-quadrant? p) (leave-quadrant game-state factor coord dir-vec)
          (hit-item? (get-in @game-state [:current-sector]) p) (bad-nav game-state factor p dir-vec)
          (<= i 0 ) (set-sector-position game-state factor p)
          :else (recur (map + p dir-vec) (dec i))))

      (swap! game-state assoc-in [:current-sector 
                                  (u/coord-to-index 
                                    (get-in @game-state [:enterprise :sector]))] 
             1)
      (merge @game-state {:quads []})
      )))

