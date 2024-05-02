(ns quanta.notebook.study.astro-moon
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.interact.template :refer [load-template show-result]]
   [ta.algo.backtest :refer [backtest-algo backtest-algo-date]]
   [ta.trade.backtest.from-position :refer [signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]
   [ta.viz.trade.core :refer [roundtrip-stats-ui]]))

(def template (load-template :moon-signal))

;template

;; here we use the default options from the template; but
;; we could change them if we wanted to.

(def algo-spec (-> (:algo template)
                   (assoc :trailing-n 5000)))

(def ds @(backtest-algo-date :bardb-dynamic  algo-spec
         (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]")))

;ds

(def roundtrips (signal->roundtrips ds))

;roundtrips

(-> roundtrips 
    roundtrip-stats
    roundtrip-stats-ui)

