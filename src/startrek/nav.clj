(ns startrek.nav
   (:require [clojure.data.generators :as gen]))

(declare get-course get-warp-factor pick-course pick-warp-factor)

(defn set-course
  [game-state]
  (let [course (pick-course) factor (pick-warp-factor)]
    ))

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
