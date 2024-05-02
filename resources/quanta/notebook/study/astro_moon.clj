(ns quanta.notebook.study.astro-moon
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.interact.template :refer [load-template show-result]]
   [ta.algo.backtest :refer [backtest-algo backtest-algo-date]]
   [ta.trade.backtest.from-position :refer [signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]
   [ta.viz.trade.core :refer [roundtrip-stats-ui]]
   [clojure.pprint :refer [print-table]]
   ))

(def end-date (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]"))

(def template (load-template :moon-signal))

(def algo-spec (-> (:algo template)
                   (assoc :trailing-n 5000)))

(def ds @(backtest-algo-date :bardb-dynamic  algo-spec end-date))

(-> (signal->roundtrips ds) 
    roundtrip-stats
    roundtrip-stats-ui)

(defn backtest-asset [asset market importer]
  (let [ds @(backtest-algo-date :bardb-dynamic (assoc algo-spec 
                                                      :asset asset 
                                                      :import importer
                                                      :calendar [market :d]) end-date)
        stats (-> ds signal->roundtrips roundtrip-stats) ]
    ;(:nav stats)
    (-> stats  :metrics :nav (assoc :asset asset))
    ))

(backtest-asset "QQQ" :us :kibot)

(defn backtest-asset-vec [[asset market importer]]
  (backtest-asset asset market importer))

(defn backtest-assets [specs]
  (map backtest-asset-vec specs))

(->> [["BTCUSDT" :crypto :bybit]
     ["ETHUSDT" :crypto :bybit]
     ["QQQ" :us :kibot] ; nasdaq
     ["DIA" :us :kibot]
     ["IWV" :us :kibot] ; russell 3000
     ["IVV" :us :kibot] ; s&p 500
     ["IWC" :us :kibot] ; microcap
     ["EWG" :us :kibot] ; germany
     ["GLD" :us :kibot]
     ["TLT" :us :kibot]]
     (backtest-assets)
     (print-table [:asset :cum-pl :max-dd]))

; |  :asset |            :cum-pl |            :max-dd |
; |---------+--------------------+--------------------|
; | BTCUSDT |   280842.456105476 |  59260.29223448355 |
; | ETHUSDT |  119845.9475714425 |  44907.61984968792 |
; |     QQQ |  243214.2969566565 |  71160.42545192914 |
; |     DIA | 165950.01197777427 |   51930.3246721857 |
; |     IWV | 194329.22240755218 | 58578.673289595856 |
; |     IVV | 184952.03789475068 | 59568.232924015785 |
; |     IWC |  197978.1907377494 |  74665.74178554882 |
; |     EWG |  150652.3397016863 |  66725.73139433426 |
; |     GLD | 121458.42789512922 |  44041.32119466996 |
; |     TLT | 18296.130723755196 |  41106.03408409166 |


