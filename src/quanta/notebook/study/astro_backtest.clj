(ns quanta.notebook.study.astro-backtest
  (:require
   [tick.core :as t]
   [ta.interact.template :refer [load-template show-result]]
   [ta.algo.backtest :refer [backtest-algo-date]]))

(def template (load-template :astro-chart))

;; here we use the default options from the template; but
;; we could change them if we wanted to.

(def algo-spec (:algo template))

(def r @(backtest-algo-date :bardb-dynamic  algo-spec
         (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]")))

(show-result template r :chart)

(def r2 @(backtest-algo-date :bardb-dynamic  algo-spec
         (t/zoned-date-time)))

(show-result template r2 :chart)

 
 
