(ns astro.algo.moon
  (:require
   [tablecloth.api :as tc]
   [ta.indicator :refer [prior]]
   [astro.moon :refer [inst->moon-phase-kw phase->text]]))

(defn moon-algo [_env _opts bar-ds]
  (let [date (:date bar-ds)
        phase (map inst->moon-phase-kw date)
        phase-text (map phase->text phase)
        phase-prior (prior phase)
        phase-change (map (fn [p p1]
                            (if (= p p1) nil phase))
                          phase phase-prior)]
    (tc/add-columns bar-ds
                    {:moon-phase phase
                     :moon-phase-text phase-text
                     :moon-phase-prior phase-prior
                     :moon-phase-change phase-change})))



