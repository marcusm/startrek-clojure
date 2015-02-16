(ns startrek.utils
   (:require [clojure.data.generators :as gen])
   (:require [clojure.math.numeric-tower :as math]))

;; global constants
(def dim           9)
;; sector map values
(def enterprise-id 1)
(def klingon-id    2)
(def base-id       3)
(def star-id       4)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Wraps the random distribution methods so I can swap them out when testing.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; randomness wrappers
(declare gen-idx gen-idx gen-double gen-uniform)

(defn gen-idx [] (gen/uniform 0 dim))

(defn gen-double [] (gen/double))

(defn gen-uniform [a b]  (gen/uniform a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some common math functions needed in several places.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- sqr
    "Uses the numeric tower expt to square a number"
      [x]
        (math/expt x 2))

(defn- euclidean-squared-distance
  "Computes the Euclidean squared distance between two sequences"
  [a b]
  (reduce +  (map  (comp sqr -) a b)))

(defn euclidean-distance
  "Computes the Euclidean distance between two sequences"
  [a b]
  (math/sqrt (euclidean-squared-distance a b)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used to extract or change shape of map data.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn strip-x-y [a-map]
  (let [{:keys [x y]} a-map]
    [x y]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used for indexing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn coord-to-index [coord]
  (+ (first coord) (* (second coord) dim)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used for text output
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def messages (atom []))

(defn message 
  ([] (swap! messages conj ""))
  ([text & rest] (swap! messages conj text rest)))
