(ns startrek.ui.drawing
  (:require [lanterna.screen :as s]
            [clojure.string :as string]))


(def screen-size [80 24])

(defn clear-screen [screen]
  (let [[cols rows] screen-size
        blank (apply str (repeat cols \space))]
    (doseq [row (range rows)]
      (s/put-string screen 0 row blank))))


(defmulti draw-ui
  (fn [ui game screen]
    (:kind ui)))


(defmethod draw-ui :start [ui game screen]
  (s/put-string screen 0 0 "Welcome to the Caves of Clojure!")
  (s/put-string screen 0 1 "Press any key to continue.")
  (s/put-string screen 0 2 "")
  (s/put-string screen 0 3 "Once in the game, you can use enter to win,")
  (s/put-string screen 0 4 "and backspace to lose."))

(defmethod draw-ui :seed [ui game screen]
  (s/put-string screen 0 0 "You can replay a game or share a game")
  (s/put-string screen 0 1 "by re-using a seed. <Press enter when done.>")
  (s/put-string screen 0 2 "Seed: ")
  (println " two " (game :seed))
  (s/put-string screen 5 2 (string/reverse (apply str (game :seed))))
  (s/move-cursor screen (+ 5 (count (game :seed))) 2))


(defmethod draw-ui :play [ui game screen]
  (s/put-string screen 0 0 "start the game!"))

(defmethod draw-ui :win [ui game screen]
  (s/put-string screen 0 0 "Congratulations, you win!")
  (s/put-string screen 0 1 "Press escape to exit, anything else to restart."))


(defmethod draw-ui :lose [ui game screen]
  (s/put-string screen 0 0 "Sorry, better luck next time.")
  (s/put-string screen 0 1 "Press escape to exit, anything else to restart."))


(defn draw-game [game screen]
  (clear-screen screen)
  (doseq [ui (:uis game)]
    (draw-ui ui game screen))
  (s/redraw screen))
