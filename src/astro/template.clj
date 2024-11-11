(ns astro.template
  (:require
   [quanta.bar.env :refer [get-trailing-bars]]
   [astro.indicator.moon]
   [astro.indicator.planets]
   [astro.plot]
   [quanta.dali.plot :as plot]))

(def astro-chart
  {:id :astro-chart
   :algo {:algo {:calendar [:crypto :m]
                 :fn astro.indicator.planets/astro-algo
                 :sp? false
                 :env? false}}
   :options []
   :chart {:viz astro.plot/astro-hiccup
           :key :algo}})


(def moon-phase
  {:id :moon-phase
   :algo {:* {:asset "BTCUSDT"}
          :bars {:calendar [:crypto :d]
                 :fn get-trailing-bars
                 :bardb :nippy
                 :trailing-n 600}
          :moon {:formula [:bars]
                 :fn astro.indicator.moon/moon-algo}}
   :options [{:type :select
              :path [:* :asset]
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   ; chart
   :chart {:key :moon
           :viz plot/highstock-ds
           :viz-options {:charts [{:bar {:type :ohlc
                                         :mode :candle}
                                   ;:close {:type :line :color "black"}
                                   :moon-phase {:type :flags
                                                :v2style {:full "url(/r/astro/moon-filled.svg)"}}}
                                  {:volume {:type :column :color "red"}}]}}
   ;table
   :table {:key :moon
           :env? false
           :viz plot/rtable-ds
           :viz-options {:columns [{:path :date :max-width "60px"}
                                   {:path :close}
                                   {:path :moon-phase}
                                   {:path :moon-phase-prior}
                                   {:path :moon-phase-text}]}}})


(def moon-signal
  {:id :moon-signal
   :algo {:* {:asset "BTCUSDT"}
          :bars {:calendar [:crypto :d]
                 :fn get-trailing-bars
                 :bardb :nippy
                 :trailing-n 600}
          :moon {:formula [:bars]
                 :fn astro.indicator.moon/moon-signal-indicator}}
   :options [{:type :select
              :path [:* :asset]
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   ; chart
   :chart {:viz  plot/highstock-ds
           :key :moon
           :viz-options {:charts [{:bar {:type :ohlc
                                          :mode :candle}
                                   :moon-phase {:type :flags
                                                :v2style {:full "url(/r/astro/moon-filled.svg)"}}}
                                  {:volume {:type :column :color "red"}}]}}
   ;table
   :table {:viz plot/rtable-ds
           :key :moon
           :env? false
           :viz-options {:columns [{:path :date :max-width "60px"}
                                   {:path :close}
                                   {:path :moon-phase}]}}})