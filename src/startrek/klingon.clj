(ns startrek.klingon
   (:require [startrek.utils :as u :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Klingons get to attack too.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- klingon-shot-strength
  [klingon enterprise]
  (* (/ (get-in klingon [:energy])
        (euclidean-distance (:sector klingon) enterprise))
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
      (u/message (format "%3.1f UNIT HIT ON ENTERPRISE FROM SECTOR %s   (%3.1f LEFT)" 
                     (:hit r)
                     (point-2-str (:sector klingon))
                     (max 0.0 (get-in r [:enterprise :shields]))))
        (:enterprise r))))

(defn klingon-turn 
  "Sad fact for the player, the klingons shoot first. They don't have any AI
  so this is their only chance to hurt the player. All klingons get to shot
  prior to the player completing any command that results in the enterprise
  attacking or moving."
  [enterprise klingons]
  (reduce klingon-attack enterprise klingons))
