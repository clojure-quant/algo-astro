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
          :algo 'astro.algo.planets/astro-algo}
   :options [{:type :select
              :path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   :table {:viz 'ta.viz.ds.rtable/rtable-render-spec
           :viz-options {:class "table-head-fixed padding-sm table-red table-striped table-hover"
                         :style {:width "50vw"
                                 :height "40vh"
                                 :border "3px solid green"}
                         :cols [{:path :date :max-width "60px"}
                                {:path :close}
                                {:path :moon-phase}]}}
   :chart {:viz 'ta.viz.ds.highchart/highstock-render-spec
           :viz-options {:chart {:box :fl}
                         :charts [{:close :candlestick #_:ohlc
                                   :moon-phase :flags}
                                  {:volume {:type :column :color "red"}}]}}})