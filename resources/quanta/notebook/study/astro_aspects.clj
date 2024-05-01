(ns quanta.notebook.study.astro-aspects
  (:require
   [tick.core :as t]
   [astro]
   [astro.aspect :as aspect]))

;; current body positions

(-> (astro/calc-date (t/instant))
    astro/print-point-table)

;; current aspects

(-> (aspect/aspects-for-date (t/instant))
    aspect/print-aspects)

; historic aspects

(def window
  (aspect/date-range (t/instant "2024-01-01T00:00:00Z")
                     (t/instant "2024-05-01T00:00:00Z")))
 

(let [aspects (aspect/calc-aspect-durations window)]
  (spit "target/webly/aspects.edn" (pr-str aspects))
  (aspect/print-aspects-window aspects))
