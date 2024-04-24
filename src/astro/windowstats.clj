(ns astro.windowstats
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [ta.indicator.date :refer [select-rows-interval]]
   [ta.indicator.returns :refer [log-return]]))

;; interval return

(defn select-first-mid-last [ds]
  (let [index-start 0
        c (tc/row-count ds)
        index-end c
        index-end (if (= 0 index-end) 0 (dec index-end))
        index-mid (int (/ (+ index-start index-end) 2))]
    ;(println "indices: " [index-start index-mid index-end])
    (tc/select-rows ds [index-start index-mid index-end])))

(defn trend [chg-left chg-right]
  (cond
    (and (> chg-left 0.0) (> chg-right 0.0)) 1.0
    (and (< chg-left 0.0) (< chg-right 0.0)) 1.0
    :else 0.0))

(defn- p-row-index-in-range [index-begin index-end]
  (fn [{:keys [index]}]
    (and (>= index index-begin)
         (<= index index-end))))

(defn select-window  [df idx-start idx-end]
  (tc/select-rows df (p-row-index-in-range idx-start idx-end)))

(defn window-stats [ds dt-start dt-end]
  (let [ds-interval (select-rows-interval ds dt-start dt-end)
        rc (tc/row-count ds-interval)]
    (if (> rc 0)
      (let [ds-first-mid-last (select-first-mid-last ds-interval)
            vec-first-mid-last (:close ds-first-mid-last)
            [first mid last] vec-first-mid-last
            change (-> (/ last first) (- 1.0) (* 1000.0))
            change-left (-> (/ mid first) (- 1.0) (* 1000.0))
            change-right (-> (/ last mid) (- 1.0) (* 1000.0))]
        {:bars rc
         :chg-l (int change-left)
         :chg-r (int change-right)
         :chg (int change)
         :trend (trend change-left change-right)})
      {:chg 0 :bars 0
       :chg-l 0 :chg-r 0 :trend 0.0})))