(ns startrek.ui.input
  (:use [startrek.ui.core :only [->UI]])
  (:require [lanterna.screen :as s]))

(def valid-seed (range (int \0) (inc (int \9))))
(defn seed-input-valid?
  [input]
  (not (nil?
         (some #{(int input)} valid-seed))))

(defmulti process-input
  (fn [game input]
    (:kind (last (:uis game)))))

(defmethod process-input :start [game input]
  (-> game
    (assoc :uis [(->UI :seed)])))

(defmethod process-input :seed [game input]
  (if (= input :enter)
    (assoc game :uis [(->UI :play)])
    (if (and (not (keyword? input)) (seed-input-valid? input))
      (-> game
      (assoc :seed (conj (game :seed) input)))
    (assoc game :uis [(->UI :seed)]))))


(defmethod process-input :win [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))

(defmethod process-input :lose [game input]
  (if (= input :escape)
    (assoc game :uis [])
    (assoc game :uis [(->UI :start)])))

(defn get-input [game screen]
  (assoc game :input (s/get-key-blocking screen)))
