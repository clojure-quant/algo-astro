(ns quanta.notebook.study.astro-moon
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.interact.template :refer [load-template show-result]]
   [ta.algo.backtest :refer [backtest-algo backtest-algo-date]]))

(def template (load-template :moon-chart))

template

;; here we use the default options from the template; but
;; we could change them if we wanted to.

(def algo-spec (:algo template))

algo-spec

(def ds @(backtest-algo-date :bardb-dynamic  algo-spec
         (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]")))


(tc/select-columns ds [:date :close :moon-phase :moon-phase-text])

(show-result template ds :chart)


 
 
