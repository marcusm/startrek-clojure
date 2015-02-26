(ns startrek.utils
   (:require [clojure.data.generators :as gen])
   (:require [clojure.math.numeric-tower :as math]))

;; global constants
(def dim           8)
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

(defn gen-idx [] (gen/uniform 1 (+ 1 dim)))

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

(defn point-2-str [point]
  (format "%d,%d" (first point) (second point)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used for indexing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn coord-to-index [coord]
  (+ (dec (first coord)) (* (dec (second coord)) dim)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Common test methods
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn leave-quadrant? [coord]
  (some true? (concat (map #(< % 0.5) coord)
                      (map #(>= % 8.5) coord))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used for text output
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def messages (atom []))

(defn message 
  "All print side effects are written to here by default. This is to reduce spam
  during unit tests or while testing code in the REPL. During actual execution,
  the main game loop rebinds this to println."
  ([] (swap! messages conj ""))
  ([text & rest] (swap! messages conj text rest)))
