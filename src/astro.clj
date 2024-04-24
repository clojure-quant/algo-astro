(ns astro
  (:require
   [ephemeris.core :refer [calc]]
   [ephemeris.time :refer [utc-to-jd]]
   [tick.core :as t]
   [clojure.pprint :refer [print-table]])
  (:import (swisseph SwissEph SweConst)))


(def planet-dict 
  {:Moon  "☾"
   :Mercury "☿"
   :Venus "♀︎"
   :Sun "☉"
   :Mars "♂︎"
   :Jupiter "♃"
   :Saturn "♄"
   :Neptune "♆"
   :TrueNode "☊"
   :Pluto "♇"
   :Uranus "♅"
   })

(defn get-body-text [b]
  ;(warn "body: " b)
  (or (get planet-dict b) "?"))


; https://github.com/astrolin/ephemeris/blob/develop/src/clj/ephemeris/points.clj

(def sign-dict
  {0 {:sign :aries :dstart 0 :sun-time "MAR 21 - APR 19" :text "♈︎"} ; 	Aries (Ram)	U+2648
   1 {:sign :taurus :dstart 30 :sun-time "APR 20 - MAY 20" :text "♉︎"}  ; 	Taurus (Bull)	U+2649
   2 {:sign :gemini :dstart 60 :sun-time "MAY 21 - JUN 20" :text "♊︎"} ; 	Gemini (Twins)	U+264A
   3 {:sign :cancer :dstart 90 :sun-time "JUN 21 - JUL 22" :text "♋︎"} ; 	Cancer (Crab)	U+264B
   4 {:sign :leo :dstart 120 :sun-time "JUL 23 - AUG 22" :text "♌︎"} ; 	Leo (Lion)	U+264C
   5 {:sign :virgio :dstart 150 :sun-time "AUG 23 - SEP 22" :text "♍︎"} ; 	Virgo (Virgin)	U+264D
   6 {:sign :libra :dstart 180 :sun-time "SEP 23 - OCT 22" :text "♎︎"} ; 	Libra (Scale)	U+264E
   7 {:sign :scorpio :dstart 210 :sun-time "OCT 23 - NOV 21" :text "♏︎"} ; Scorpio (Scorpion)	U+264F
   8 {:sign :sagittarius :dstart 240 :sun-time "NOV 22 - DEC 21" :text "♐︎"} ; 	Sagittarius (Archer)	U+2650
   9 {:sign :capricorn :dstart 270 :sun-time "DEC 22 - JAN 19" :text "♑︎"}  ;(Sea-Goat)	U+2651
   10 {:sign :aquarius :dstart 300 :sun-time "JAN 20 - FEB 18" :text  "♒︎"} ; (Waterbearer)	U+2652
   11 {:sign :piscies :dstart 330 :sun-time "FEB 19 - MAR 20" :text "♓︎"} ; (Fish) U+2653
   })

(defn get-text [i]
  (-> (get sign-dict i)
      :text))

;(get-text 3)

(defn deg->sign [d]
  (let [q (quot d 30.0)
        q (int q)]
    (get sign-dict q)))

(defn deg->rad [d]
  ; Degrees x (π/180) = Radians
  (* d (/ Math/PI 180.0)))

(defn rad->deg [r]
  ; Radians  × (180/π) = Degrees
  (* r (/ 180.0 Math/PI)))

(def geo-req {:utc "2022-03-15T00:13:00Z"
                   ;"2009-02-03T21:43:00Z"
              :geo {:lat 40.58 :lon -74.48}
              :angles [:Asc :MC :Angle]
              :points [:Sun :Moon
                       :Mercury :Venus :Mars
                       :Jupiter :Saturn :Uranus :Neptune :Pluto
                       ;:Body 
                       ;:Chiron :Pholus :Ceres :Pallas :Juno :Vesta 
                       :TrueNode]})

(defn utc-date-str [dt]
  (-> dt t/instant str))

#_(defn dt-format [dt]
  (let [dtz (t/zoned-date-time dt)]
    (t/format (t/formatter :iso-instant) dtz))) ; :iso-date

(defn calc-date [dt]
  (calc (assoc geo-req :utc (utc-date-str dt))))

(defn point-table [res]
  (let [points (:points res)]
    (map (fn [[n v]]
           (let [lon (:lon v)
                 sign (deg->sign lon)]
            ;(info "sign: " sign)
             (merge v (assoc sign :name n))))
         points)))

(defn print-point-table [res]
  (->> res 
      point-table 
      (print-table [:name :text :sign :lon :lat :sdd
                    ; :sun-time dstart
                    ])))



(defn horizontal-pos
  "calc the azimuth angle from the observers view to the body.
   returns double value. 0° = north, 90° = east, 180° = south and 270° = west
   doc: https://www.astro.com/swisseph/swephprg.htm#_Toc112948998"
  [utc-str {:keys [type lon lat height atpress attemp]} body-pos]
  (let [ut (utc-to-jd utc-str)
        calc_flag (case type
                    :ecliptic (SweConst/SE_ECL2HOR)
                    :equatorial (SweConst/SE_EQU2HOR))
        geopos (double-array [(if lon lon 0)
                              (if lat lat 0)
                              (if height height 0)])
        atpress (if atpress atpress 0)
        attemp (if attemp attemp 0)
        xin (double-array [(:lon body-pos) (:lat body-pos) (if (:dist body-pos)
                                                             (:dist body-pos)
                                                             0)])
        xaz (double-array 3)
        sw (SwissEph.)
        _ (.swe_azalt sw ut calc_flag geopos atpress attemp xin xaz)
        [azimuth true-altitude apparent-altitude] xaz]
    ; convert azimuth value (south to north)
    (if (>= azimuth 180)
      (- azimuth 180)
      (+ azimuth 180))))


(comment
  (let [query (assoc geo-req :utc "2022-06-15T13:00:00Z")
        res (calc query)
        {:keys [points angles houses wanted result]} res
        {:keys [utc geo]} wanted]
    (horizontal-pos utc (merge {:type :ecliptic} geo) (:Sun points)))

  )

