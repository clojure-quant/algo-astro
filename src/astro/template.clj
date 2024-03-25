(ns astro.template)

(def astro-chart
  {:id :astro-chart
   :algo {:type :time
          :calendar [:crypto :m]
          :algo 'astro.algo.planets/astro-algo}
   :options [{:type :select
              :path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   :chart {:viz 'astro.hiccup/astro-hiccup}})

(def moon-chart
  {:id :moon-chart
   :algo {:type :trailing-bar
          :calendar [:crypto :d]
          :trailing-n 600
          :asset "BTCUSDT"
          :import :bybit
          :algo 'astro.algo.moon/moon-algo}
   :options [{:type :select
              :path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   ; chart
   :chart {:viz 'ta.viz.ds.highchart/highstock-render-spec
           :viz-options {:chart {:box :fl}
                         :charts [{:close :candlestick #_:ohlc
                                   :moon-phase {:type :flags
                                                :v2style {:full "url(/r/astro/moon-filled.svg)"}}}
                                  {:volume {:type :column :color "red"}}]}}
   ;table
   :table {:viz 'ta.viz.ds.rtable/rtable-render-spec
           :viz-options {:columns [{:path :date :max-width "60px"}
                                    {:path :close}
                                    {:path :moon-phase}
                                    {:path :moon-phase-prior}
                                    {:path :moon-phase-change}]}}
   })