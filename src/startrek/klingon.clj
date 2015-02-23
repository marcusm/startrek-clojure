(ns startrek.klingon
   (:require [startrek.utils :as u :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Klingons get to attack too.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- klingon-shot-strength
  [klingon enterprise]
  (* (/ (get-in klingon [:energy])
        (euclidean-distance (strip-x-y klingon) enterprise))
     2
     (gen-double)))

(defn klingon-dead? [klingon]
  (not (pos? (:energy klingon))))

(defn- klingon-shot [enterprise klingon]
  (let [h (klingon-shot-strength klingon (-> enterprise :sector))]
    {:hit h :enterprise (update-in enterprise [:shields] - h)}))

(defn- klingon-attack [enterprise klingon]
  (if (or (klingon-dead? klingon) (< (:shields enterprise) 0))
    enterprise
    (let [r (klingon-shot enterprise klingon)]
      (u/message (format "%3.1f UNIT HIT ON ENTERPRISE FROM SECTOR %d,%d   (%3.1f LEFT)" 
                     (:hit r)
                     (:x klingon)
                     (:y klingon)
                     (max 0.0 (get-in r [:enterprise :shields]))))
        (:enterprise r))))

(defn klingon-turn [enterprise klingons]
  (reduce klingon-attack enterprise klingons))
