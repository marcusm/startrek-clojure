(ns startrek.nav
   (:require [clojure.data.generators :as gen]))

(declare get-course get-warp-factor)

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
        (< factor 0) (recur)
        (> factor 9) (recur)
        :else factor))))

; (defn pick-warp-factor
;   [enterprise]
;   (let [factor (select-warp-factor)]
;    ))
