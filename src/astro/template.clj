(ns astro.template)


(def astro-chart
  {:id :astro-chart
   :algo {:type :time
          :algo 'astro.algo/astro-algo
          :calendar [:crypto :m]}
   :options [{:type :select
              :path :asset
              :name "Asset"
              :spec ["BTCUSDT" "ETHUSDT"]}]
   :chart {:viz 'astro.hiccup/astro-hiccup}})