(ns startrek.core
  (:require [clojure.data.generators :as gen]
            [clojure.core.reducers :as r]))
; The global game datastructure
(def game-state (atom 0))

(defn fill-test [x]
  (+ 50 x))

(defn get-klingons []
  (let [chance (gen/double)]
    (cond
      (> chance 0.98) 3
      (> chance 0.95) 2
      (> chance 0.8)  1
      :else 0)))

(def red-test  (r/filter #(> 0  (:klingon %))))

(defn get-starbases []
  (let [chance (gen/double)]
    (cond
      (> chance 0.96) 1
      :else 0))) 

(defn get-stars [] (gen/uniform 1 9))

(defn quadrant-klingon-count [quadrants]
  (reduce + (flatten (map #(map :klingons %) quadrants))))
(defn quadrant-starbase-count [quadrants]
  (reduce + (flatten (map #(map :bases %) quadrants))))

(defn create-quadrants []
  (to-array-2d 
    (partition  9
               (for  [y  (range 1 10) x  (range 1 10)]  
                 {:x x :y y :klingons (get-klingons) :stars (get-stars) :bases (get-starbases)}))))

(defn reset-quadrants []
  (loop []
  (let [quadrants (create-quadrants)]
    (cond
      (<= (quadrant-klingon-count quadrants) 0) (recur)
      (<= (quadrant-starbase-count quadrants) 0) (recur)
      :else quadrants))))

; reset enterprise state
(defn reset-enterprise []
  {:damage 
   {:short_range_sensors 0
    :computer_display 0
    :long_range_sensors 0
    :phasers 0
    :warp_engines 0
    :photon_torpedo_tubes 0
    :shields 0}
   :photon_torperdoes 10
   :energy 3000
   :is_docked false
   })

; fetch default game state
(defn new-game-state []
  (defn gen-0-8 [] (gen/uniform 0 8))
  (defn gen-stardate [] (* 100 (+ 20 (gen/uniform 1 21))))

  {:enterprise (reset-enterprise)
  :quadrant (reset-quadrants)
  :sector {:x (gen-0-8) :y (gen-0-8)}
  :stardate {:start (gen-stardate) :end 30}}
  )

(defn print-instructions []
  (println "INSTRUCTIONS"))

(def seed-rand)

(defn setup-universe []
  (println "Setting up the universe"))
(defn enter-quadrant []
  (println "Enter the quadrant"))
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
  (println)
  )

(defn play-game []
  (setup-universe)
  (let [stop-condition (ref false)]
    (while (and (not (deref stop-condition)))
      ; check to see if the Enterprise is destroyed

      (enter-quadrant)
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

; only needed for reading numbers from command line
(let [m (.getDeclaredMethod clojure.lang.LispReader
                            "matchNumber"
                            (into-array [String]))]
  (.setAccessible m true)
  (defn parse-number [s]
    (.invoke m clojure.lang.LispReader (into-array [s]))))


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
      (print-instructions)))

  ; This is unsafe if the user does not provide an int
  ; for a seed. Need to fix that.
  (println)
  (println "ENTER SEED NUMBER ")
  (let [seed (parse-number (read-line))]
    (gen/*rnd* seed))

  (play-game))
