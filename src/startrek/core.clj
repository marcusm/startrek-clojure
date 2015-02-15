(ns startrek.core
   (:require [clojure.java.io :as io]
             [clojure.edn :as edn]
             [clojure.data.generators :as gen]
             [startrek.random :as r]
             [startrek.klingon :as k]
             [startrek.world :as w])
   (:gen-class :main true))

(def game-state (atom {}))

(def pages
  (->>  "instructions.edn"
       io/resource
       slurp
       edn/read-string
       (map :page)))

; only needed for reading numbers from command line
(let [m (.getDeclaredMethod clojure.lang.LispReader
                            "matchNumber"
                            (into-array [String]))]
  (.setAccessible m true)
  (defn parse-number [s]
    (.invoke m clojure.lang.LispReader (into-array [s]))))

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

(defn setup-universe []
  (w/new-game-state game-state)
  (w/init-sector game-state))
(defn set-course []
  (println "Set the course"))
(defn short-range-scan []
  (println "short range scan"))
(defn long-range-scan []
  (println "long range scan"))
(defn fire-phasers []
  (println "fire phasers"))
(defn fire-torpedoes []
  (println "fire torpedoes"))
(defn shield-control []
  (println "shield control"))
(defn damage-control-report []
  (println "damage control report"))
(defn library-computer []
  (println "library computer"))

(defn game-over-destroyed [game-state]
  (println)
  (println "THE ENTERPRISE HAS BEEN DESTROYED. THE FEDERATION WILL BE CONQUERED")
  (println (format "THERE ARE STILL %s KLINGON BATTLE CRUISERS" (w/remaining-klingon-count (:quads @game-state))))
  true)

(defn game-over-powerless [game-state]
  (println "THE ENTERPRISE IS DEAD IN SPACE. IF YOU SURVIVE ALL IMPENDING")
  (println "ATTACK YOU WILL BE DEMOTED TO THE RANK OF PRIVATE")
  (let [enterprise (k/klingon-turn (get-in @game-state [:enterprise]) (get-in @game-state [:current-klingons]))]
    (if (neg? (get-in enterprise [:shields]))
      (game-over-destroyed game-state)
      (println (format "THERE ARE STILL %s KLINGON BATTLE CRUISERS" 
                       (w/remaining-klingon-count (:quads @game-state))))))
  true)

(defn game-over-success [game-state]
  (println)
  (println "THE LAST KLINGON BATTLE CRUISER IN THE GALAXY HAS BEEN DESTROYED")
  (println "THE FEDERATION HAS BEEN SAVED !!!")
  (println)
  (let [k (get-in @game-state [:starting-klingons]) 
        start (get-in @game-state [:stardate :start])
        current (get-in @game-state [:stardate :current])]
    (println (format "YOUR EFFICIENCY RATING = %5.2f" (* 1000 (/ k (- start current)))))))

(defn game-over? [game-state]
  (cond
    (neg? (get-in @game-state [:enterprise :shields])) (game-over-destroyed game-state)
    (and (<= (get-in @game-state [:enterprise :shields]) 1)
         (zero? (get-in @game-state [:enterprise :energy]))) (game-over-powerless game-state)
    (zero? (w/remaining-klingon-count (:quads @game-state))) (game-over-success game-state)
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

(defn play-game []
  (setup-universe)
  (let [stop-condition (ref false)]
    (while (not (deref stop-condition))
      ; check to see if the Enterprise is destroyed

      (println "COMMAND")
      (let [choice (read-line)]
        (condp = choice
          "0" (set-course)
          "1" (short-range-scan)
          "2" (long-range-scan)
          "3" (fire-phasers)
          "4" (fire-torpedoes)
          "5" (shield-control)
          "6" (damage-control-report)
          "7" (library-computer)
          "q" (dosync (alter stop-condition (fn [_] true)))
          (command-help)
          )))))

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
  (play-game)))
