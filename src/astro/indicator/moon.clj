(ns astro.indicator.moon
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.indicator :refer [prior]]
   [astro.moon :refer [inst->moon-phase-kw phase->text]]
   [quanta.dali.ds :refer [sanitize-ds]]
   ))

(defn moon-phase [col-date]
  (dtype/emap inst->moon-phase-kw :keyword col-date))

(defn moon-algo [_opts bar-ds]
  (let [date (:date bar-ds)
        phase (moon-phase date) ;(map inst->moon-phase-kw date)
        phase-text (map phase->text phase)
        phase-prior (prior phase)
        ; 2024-11-02 awb99: phase-change would bring reify errors to column. Not sure why.
        ;phase-change (map (fn [p p1]
        ;                    (if (= p p1) nil phase))
        ;                  phase phase-prior)
        ]
    (-> bar-ds
       (tc/add-columns 
                   {:moon-phase phase
                    :moon-phase-text phase-text
                    :moon-phase-prior phase-prior
                    ;:moon-phase-change phase-change
                    })
        ;(sanitize-ds)
        )))

(defn moon-phase->signal [phase]
  (if phase
    (case phase
      :i1 :flat
      :full :long
      :hold)
    :hold))

(defn moon-signal [moon-phase]
  (dtype/emap moon-phase->signal :keyword moon-phase))

(defn moon-signal-indicator [opts bar-ds]
  (let [col-date (:date bar-ds)
        col-moon-phase (moon-phase col-date)
        col-moon-signal (moon-signal col-moon-phase)]
    (-> bar-ds
        (tc/add-columns {:moon-phase col-moon-phase
                         :signal col-moon-signal}))))

