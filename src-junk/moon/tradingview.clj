(ns astro.moon.tradingview
   (:require
    [tablecloth.api :as tc]
    [tech.v3.datatype :as dtype]
    [astro.moon :refer [inst->moon-phase-kw phase->text]]
    [ta.tradingview.chart.shape :as shapes]
    [ta.tradingview.chart.plot :refer [plot-type]]
    [ta.tradingview.chart.color :refer [color]]))

;; SHAPES 

(defn cell->shape [epoch value]
  (let [text (phase->text value)]
    (shapes/text epoch text)))

(comment
  (cell->shape 3 :full)
  (cell->shape 3 :new)
 ;
  )

(defn moon-phase-shapes [user-options epoch-start epoch-end]
  ; todo: change api, so that it will add the ds also.
  (shapes/algo-col->shapes
   "moon"
   user-options epoch-start epoch-end
   :phase cell->shape))

(defn fixed-shapes [user-options epoch-start epoch-end]
  [(shapes/line-vertical 1644364800) ; feb 9
   (shapes/line-vertical 1648944000) ; april 3
   (shapes/line-horizontal 350.55)
   (shapes/gann-square 1643846400 350.0 1648944000  550.0)])

(def charts [;nil ; {:trade "flags"}
           ;{:trade "chars" #_"arrows"}
             {:signal-text {:type "chars"
                            :char "!"
                            :textColor (color :steelblue)
                            :title "moon-phase-fullmoon" ; title should show up in pane settings
                            }}
             {:volume {:type "line" :plottype (plot-type :columns)}}])
