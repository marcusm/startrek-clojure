(ns startrek.computer
   (:require [clojure.math.numeric-tower :as math])
   (:require [startrek.random :as r])
   (:require [startrek.utils :as u :refer :all])
   (:require [startrek.enterprise :as e])
   (:require [startrek.world :as w]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions used to pick the actual computer command.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(declare galactic-record status-report photon-torpedo-data warp-distance-calculator)

(defn- pick-computer-command [] 0)

(defn- ask-computer-command []
  (u/message "COMPUTER ACTIVE AND AWAITING COMMAND ")
  (pick-computer-command))

(defn- select-computer-command []
  (loop [c (ask-computer-command)]
    (if (and (not (nil? c)) (>= c 0) (<= c 3))
      c
      (do
        (u/message "FUNCTIONS AVAILABLE FROM COMPUTER");
        (u/message "   0 = CUMULATIVE GALACTIC RECORD");
        (u/message "   1 = STATUS REPORT");
        (u/message "   2 = PHOTON TORPEDO DATA");
        (u/message "   3 = COMPUTER NAVIGATION DATA");
        (recur (pick-computer-command))))))

(defn computer-command [game-state]
  (if (neg? (get-in @game-state [:enterprise :damage :computer_display]))
      (u/message "COMPUTER DISABLED")
      (let [command (select-computer-command)]
        (condp = command 
          0 (galactic-record game-state)
          1 (status-report game-state)
          2 (photon-torpedo-data game-state)
          3 (warp-distance-calculator game-state)
          (u/message "UNKNOWN CHOICE")))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; targeting algorithm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- compute-distance [e t]
  (u/euclidean-distance e t))

(defn- compute-direction [e k]
  (cond
    (= (first e) (first k)) (if (< (second e) (second k)) 7.0 3.0)
    (= (second e) (second k)) (if (< (first e) (first k)) 1.0 5.0)
    :else (let [delta (map - e k)
                angle (Math/atan (/ (math/abs (second delta)) (math/abs (first delta))))
                rad-angle (* 4.0 (/ angle Math/PI))]
            (if (< (first e) (first k)) 
              (if (< (second e) (second k)) 
                (- 9.0 rad-angle)
                (+ 1.0 rad-angle))
              (if (< (second e) (second k)) 
                (+ 5.0 rad-angle)
                (- 5.0 rad-angle))))))

(defn- compute-targeting-data [game-state]
  (doseq [k (get-in @game-state [:current-klingons])] 
    (let [d (compute-direction (get-in @game-state [:enterprise :sector]) [(:x k) (:y k)])]
      (u/message (format "DIRECTION %1.3f TO KLINGON AT SECTOR %s"
                       d
                       (u/point-2-str [(:x k) (:y k)]))))))

(defn photon-torpedo-data [game-state]
  (if (seq (get-in @game-state [:current-klingons]))
    (compute-targeting-data game-state)
    (u/message "NO KLINGONS IN THE CURRENT QUADRANT.")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Navigation help
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- pick-coordinate [] 4)

(defn- ask-coordinate [axis]
  (u/message "ENTER " axis "COORDINATE")
  (pick-coordinate))

(defn- select-coordinate [axis]
  (loop [c (ask-coordinate axis)]
    (if (and (not (nil? c)) (>= c 1) (<= c 8))
      c
      (recur (pick-computer-command)))))



(defn- navigation-calculator [e]
  (let [x (select-coordinate "X-AXIS")
        y (select-coordinate "Y-AXIS")]
    {:direction (compute-direction e [x y])
     :distance (compute-distance e [x y])}))

(defn warp-distance-calculator [game-state]
  (u/message "PICK THE TARGETED QUADRANT")
  (let [nav (navigation-calculator (get-in @game-state [:enterprise :quadrant]))]
    (u/message (format "COMPUTER REPORTS DIRECTION: %1.3f DISTANCE: %1.3f" 
                     (:direction nav) 
                     (double (:distance nav))))))
