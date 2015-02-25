(ns startrek.computer
   (:require [clojure.math.numeric-tower :as math])
   (:require [startrek.random :as r])
   (:require [startrek.utils :as u :refer :all])
   (:require [startrek.enterprise :as e])
   (:require [startrek.world :as w]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used to pick the actual computer command.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare galactic-record status-report photon-torpedo-data)

(defn- pick-computer-command [] 0)

(defn- ask-computer-command []
  (println "COMPUTER ACTIVE AND AWAITING COMMAND ")
  (pick-computer-command))

(defn- select-computer-command []
  (loop [c (ask-computer-command)]
    (if (and (>= c 0) (<= c 2))
      c
      (do
        (u/message "FUNCTIONS AVAILABLE FROM COMPUTER");
        (u/message "   0 = CUMULATIVE GALATIC RECORD");
        (u/message "   1 = STATUS REPORT");
        (u/message "   2 = PHOTON TORPEDO DATA");
        (recur (pick-computer-command))))))

(defn computer-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :computer_display]))
      (println "COMPUTER DISABLED")
      (let [command (select-computer-command)]
        (condp = command 
          0 (galactic-record game-state)
          1 (status-report game-state)
          2 (photon-torpedo-data game-state)
          (println "Unknown choice. Should never see this!")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The Galactic Record
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn galactic-record [game-state]
  (u/message (format "COMPUTER RECORD OF GALAXY FOR QUADRANT %s" 
                     (u/point-2-str (get-in @game-state [:enterprise :quadrant]))))

  (u/message "-------------------------------------------------")
  (loop [y 0]
    (u/message "|" (clojure.string/join " | "
                                        (take 8 (drop (* y 8) (get-in @game-state [:lrs-history]))))
               "|")
    (when (> (dec dim) y)
      (recur (inc y))))
  (u/message "-------------------------------------------------"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Status Report
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn status-report [game-state]
      (u/message "")
      (u/message "   STATUS REPORT")
      (u/message "NUMBER OF KLINGONS LEFT  = " (w/remaining-klingon-count (get-in @game-state [:quads])))
      (u/message "NUMBER OF STARDATES LEFT = " 
                 (- (+ (get-in @game-state [:stardate :start])
                       (get-in @game-state [:stardate :end]))
                    (get-in @game-state [:stardate :current])))
      (u/message "NUMBER OF STARBASES LEFT = " (w/remaining-starbase-count (get-in @game-state [:quads])))
      (u/message "")
      (e/damage-control-report-command game-state))
