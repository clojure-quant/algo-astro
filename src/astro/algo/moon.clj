(ns astro.algo.moon
  (:require 
   [tablecloth.api :as tc]
   [indicator :refer [prior]]
    [astro.moon :refer [moon-phase-from-instant phase->text]])
  )

; :text "🌑"


(defn moon-algo [env opts bar-ds]
  (let [date (:date bar-ds)
        phase (map moon-phase-from-instant date)
        phase-prior (prior phase)
        change (map (fn [p p1]
                      (if (= p p1) nil phase))
                    phase phase-prior)]
    (tc/add-column bar-ds :moon-phase change)))



