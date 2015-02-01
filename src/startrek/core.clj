(ns startrek.core)
(import '(java.util Random))

(defn print-instructions []
  (println "INSTRUCTIONS"))

(let [m (.getDeclaredMethod clojure.lang.LispReader
                            "matchNumber"
                            (into-array [String]))]
  (.setAccessible m true)
  (defn parse-number [s]
    (.invoke m clojure.lang.LispReader (into-array [s]))))

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
    (def seed-rand (java.util.Random. seed))
    (println "INITIALIZING...")
    (dotimes [i seed]
      (.nextDouble seed-rand)))

  (play-game))

(defn get-klingons []
  (+ 2 1))
