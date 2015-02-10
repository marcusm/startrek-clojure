(ns startrek.nav
   (:require [startrek.enterprise :as e])
   (:require [startrek.klingon :as k]))

(declare get-course get-warp-factor pick-course pick-warp-factor)

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
  (println " pick-factor => " enterprise)
  (loop []
    (let [factor (select-warp-factor)]
      (cond
        (and (pos? (get-in enterprise [:damage :warp_engines]))
             (> factor 0.2))
        (do (println "WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP .2") (recur))
        :else factor))))

(defn- move [game-state factor course]
  (let [n (int (* factor 8))]))

(defn set-course [game-state]
  (let [course (pick-course) factor (pick-warp-factor (:enterprise @game-state))]
    (assoc-in @game-state [:enterprise] (k/klingon-turn (:enterprise @game-state) (:current-klingons @game-state)))
    (when-not (neg? (get-in @game-state [:enterprise :shields]))
      (e/enterprise-update game-state)
      (move game-state))))

