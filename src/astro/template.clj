(ns astro.template
  (:require
   [quanta.algo.env.bars :refer [get-trailing-bars]]
   [astro.indicator.moon]
   [astro.indicator.planets]
   [astro.plot]
   [quanta.dali.plot :as plot]))

(def astro-chart
  {:id :astro-chart
   :algo {:type :time
          :calendar [:crypto :m]
          :algo astro.indicator.planets/astro-algo}
   :options [{:type :select
              :path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   :chart {:viz astro.plot/astro-hiccup}})


(defn moon-algo [opts dt]
  (->> (get-trailing-bars opts dt)
       (astro.indicator.moon/moon-algo opts)))

(def moon-phase
  {:id :moon-phase
   :algo [{:asset "BTCUSDT"
           :calendar [:crypto :d]
           :bardb :nippy
           :trailing-n 600}
          :day {:calendar [:crypto :d]
                :algo get-trailing-bars
                :bardb :nippy
                :trailing-n 1100}
          :moon-bad {:formula [:day]
                 :algo astro.indicator.moon/moon-algo}
          :moon {:calendar [:crypto :d]
                 :algo moon-algo
                 }
          
          ]
   :options [{:type :select
              :path [0 :asset]
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
           :viz plot/rtable-ds
           :viz-options {:columns [{:path :date :max-width "60px"}
                                   {:path :close}
                                   {:path :moon-phase}
                                   {:path :moon-phase-prior}
                                   {:path :moon-phase-text}]}}})

(defn moon-algo-signal [opts dt]
  (->> (get-trailing-bars opts dt)
       (astro.indicator.moon/moon-signal-indicator opts)))

(def moon-signal
  {:id :moon-signal
   :algo [{:asset "BTCUSDT"
           :calendar [:crypto :d]
           :bardb :nippy
           :trailing-n 600}
          :bars {:algo get-trailing-bars
                 :calendar [:crypto :d]
                 :bardb :nippy
                 :trailing-n 600
                 :asset "BTCUSDT"}
          :moon-bad {:formula [:bars]
                     :algo astro.indicator.moon/moon-signal-indicator}
           :moon {:calendar [:crypto :d]
                 :algo moon-algo-signal}
          
          ]
   :options [{:type :select
              :path [0 :asset]
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
           :viz-options {:columns [{:path :date :max-width "60px"}
                                   {:path :close}
                                   {:path :moon-phase}]}}})