(ns startrek.random
   (:require [clojure.data.generators :as gen]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Wraps the random distribution methods so I can swap them out when testing.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn gen-idx [dim] (gen/uniform 0 dim))

(defn gen-double [] (gen/double))

(defn gen-uniform []  (gen/uniform))
