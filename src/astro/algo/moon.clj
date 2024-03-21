(ns astro.algo.moon
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :refer [prior]]
   [astro.moon :refer [inst->moon-phase-kw phase->text]]))

(defn moon-algo [_env _opts bar-ds]
  (let [date (:date bar-ds)
        _ (println "MOON PHASE CALC..")
        phase (map inst->moon-phase-kw date)
        _ (println "CHANGE CALC..")
        phase-prior (prior phase)
        phase-change (map (fn [p p1]
                            (if (= p p1) nil phase))
                          phase phase-prior)]
    (println "moon-algo finished!")
    (tc/add-columns bar-ds
                    {:moon-phase phase
                     :moon-phase-prior phase-prior
                     :moon-phase-change phase-change})))



