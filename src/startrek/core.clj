(ns startrek.core
   (:require [clojure.java.io :as io]
             [clojure.edn :as edn]
             [clojure.data.generators :as gen]
             [startrek.random :as r]
             [startrek.utils :as u]
             [startrek.klingon :as k]
             [startrek.enterprise :as e]
             [startrek.world :as w]
             [startrek.nav :as n]
             [startrek.computer :as c])
   (:gen-class :main true))

(def game-state (atom {}))

(def pages
  (->>  "instructions.edn"
       io/resource
       slurp
       edn/read-string
       (map :page)))

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)
    (let [s2 (str "0" s)]
      (if (re-find #"^-?\d+\.?\d*$" s2)
        (read-string s2)))))

(defn print-instructions [pause]
  (loop [p pages]
    (when (seq p)
      (println (first p))
      (when (= pause 2)
        (println "PRESS A KEY TO CONTINUE...  ")
        ; (let  [cr  (ConsoleReader.)
        ;        keyint  (.readCharacter cr)]
        ;       (print keyint))

        ; (let  [term  (Terminal/getTerminal)]
        ;   (println  (char(.readCharacter term System/in)))))
      (recur (rest p))))))

(defn library-computer []
  (println "library computer"))

(defn game-over-out-of-time [game-state]
  (u/message)
  (u/message (format "IT IS STARDATE %d" (get-in @game-state [:stardate :current])))
  (u/message (format "THERE ARE STILL %s KLINGON BATTLE CRUISERS" (w/remaining-klingon-count (:quads @game-state))))
  true)

(defn game-over-destroyed [game-state]
  (u/message)
  (u/message "THE ENTERPRISE HAS BEEN DESTROYED. THE FEDERATION WILL BE CONQUERED")
  (u/message (format "THERE ARE STILL %s KLINGON BATTLE CRUISERS" (w/remaining-klingon-count (:quads @game-state))))
  true)

(defn game-over-powerless [game-state]
  (u/message "THE ENTERPRISE IS DEAD IN SPACE. IF YOU SURVIVE ALL IMPENDING")
  (u/message "ATTACK YOU WILL BE DEMOTED TO THE RANK OF PRIVATE")
  (let [enterprise (k/klingon-turn (get-in @game-state [:enterprise]) (get-in @game-state [:current-klingons]))]
    (if (neg? (get-in enterprise [:shields]))
      (game-over-destroyed game-state)
      (u/message (format "THERE ARE STILL %s KLINGON BATTLE CRUISERS" 
                       (w/remaining-klingon-count (:quads @game-state))))))
  true)

(defn game-over-success [game-state]
  (u/message)
  (u/message "THE LAST KLINGON BATTLE CRUISER IN THE GALAXY HAS BEEN DESTROYED")
  (u/message "THE FEDERATION HAS BEEN SAVED !!!")
  (u/message)
  (let [k (get-in @game-state [:starting-klingons]) 
        start (get-in @game-state [:stardate :start])
        current (get-in @game-state [:stardate :current])]
    (u/message (format "YOUR EFFICIENCY RATING = %5.2f" (* 1000.0 (/ k (- current start)))))))

(defn game-over? [game-state]
  (cond
    (neg? (get-in @game-state [:enterprise :shields])) (game-over-destroyed game-state)
    (and (<= (get-in @game-state [:enterprise :shields]) 1)
         (zero? (get-in @game-state [:enterprise :energy]))) (game-over-powerless game-state)
    (zero? (w/remaining-klingon-count (:quads @game-state))) (game-over-success game-state)
    (> (get-in @game-state [:stardate :current])
       (+ (get-in @game-state [:stardate :start])
          (get-in @game-state [:stardate :end]))) (game-over-out-of-time game-state)
    :else false))

(defn command-help []
  (println)
  (println "   0 = SET COURSE")
  (println "   1 = SHORT RANGE SENSOR SCAN")
  (println "   2 = LONG RANGE SENSOR SCAN")
  (println "   3 = FIRE PHASERS")
  (println "   4 = FIRE PHOTON TORPEDOES")
  (println "   5 = SHIELD CONTROL")
  (println "   6 = DAMAGE CONTROL REPORT")
  (println "   7 = CALL ON LIBRARY COMPUTER")
  (println))

(defn user-input []
  (parse-number (read-line)))

(defn play-game []
  (w/new-game-state game-state)
  (n/enter-quadrant game-state)
  (let [stop-condition (ref false)]
    (while (not (deref stop-condition))
      ; check to see if the Enterprise is destroyed
      (if (game-over? game-state)
        (do
          (w/new-game-state game-state)
          (n/enter-quadrant game-state))
        (do
          (w/short-range-scan-command game-state)

          (println "COMMAND")
          (let [choice (read-line)]
            (condp = choice
              "0" (n/set-course-command game-state)
              "1" (w/short-range-scan-command game-state)
              "2" (w/long-range-scan-command game-state)
              "3" (e/fire-phasers-command game-state)
              "4" (e/fire-torpedoes-command game-state)
              "5" (e/shield-control-command game-state)
              "6" (e/damage-control-report-command game-state)
              "7" (c/computer-command game-state)
              "q" (dosync (alter stop-condition (fn [_] true)))
              (command-help)
              )))))))

(defn -main [& args]
  (dotimes [i 20]
    (println))
  (println "                          STAR TREK ")
  (println)
  (println)
  (println)
  (println "ENTER 1 OR 2 FOR INSTRUCTIONS (ENTER 2 TO PAGE) ")
  (let [choice (parse-number (read-line))]
    (if (or (= choice 1) (= choice 2))
      (print-instructions choice)))

  ; This is unsafe if the user does not provide an int
  ; for a seed. Need to fix that.
  (println)
  (println "ENTER SEED NUMBER ")
  (let [seed (parse-number (read-line))]
    (binding  [gen/*rnd*  (java.util.Random. seed)])

    (with-redefs [u/message println
                  n/pick-course user-input
                  n/pick-warp-factor user-input
                  e/pick-phaser-power user-input
                  e/pick-torpedo-course user-input
                  e/pick-shield-power user-input 
                  c/pick-computer-command  user-input 
                  c/pick-coordinate  user-input 
                  ]
    (play-game))))
