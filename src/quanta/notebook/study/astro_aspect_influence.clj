(ns quanta.notebook.study.astro-aspect-influence
  (:require
   [clojure.edn :as edn]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.helper.print :refer [print-all]]
   [ta.db.bars.protocol :as b]
   [modular.system]
   [ta.calendar.core :as cal]
   [astro.aspect :as aspect]
   [astro.windowstats :refer [window-stats]]))

;; this study wants to determine, which aspects have
;; a significance on the market

;; load aspects

(defn load-aspects [filename]
  (-> (slurp filename) edn/read-string))

(defn load-aspects-until-now-no-moon []
  (-> (load-aspects "target/webly/aspects.edn")
      (aspect/select-aspects-until-now)
      aspect/remove-moon-aspects))

(def aspects (load-aspects-until-now-no-moon))

(-> aspects count)


;; load series

(def db (modular.system/system :bardb-dynamic))

(def window (cal/trailing-range [:us :d] 1000))

(def bars-ds (b/get-bars db {:asset "BTCUSDT"
                             :calendar [:us :d]
                             :import :bybit}
                         window))

bars-ds

;; aspect return

(defn- assoc-aspect-return [ds {:keys [start end] :as aspect}]
  (merge aspect (window-stats ds start end)))

(defn add-aspect-return [aspects ds]
   (map (partial assoc-aspect-return ds) aspects))

(->> (add-aspect-return aspects bars-ds)
    (take 3))



(defn aspect-group-stats [group-by ds-aspect]
  (-> ds-aspect
      (tc/group-by group-by)
      (tc/aggregate
       {:count (fn [ds]
                 (->> ds
                      :chg
                      count))
        :bars (fn [ds]
                (->> ds
                     :bars
                     fun/mean
                     int))
        :mean (fn [ds]
                (->> ds
                     :chg
                     fun/mean
                     int))
        :med (fn [ds]
               (->> ds
                    :chg
                    fun/median
                    int))
        :min (fn [ds]
               (-> (apply min (:chg ds)) int))
        :max (fn [ds]
               (-> (apply max (:chg ds)) int))
        :trend (fn [ds]
                 (->> ds
                      :trend
                      fun/mean
                      (* 100.0)
                      int))})))


(defn calc-aspect-stats [aspects ds group-by]
  (let [ds-all (->> (add-aspect-return aspects ds)
                    tc/dataset)
        ds-groups (-> (->> ds-all
                           (aspect-group-stats group-by))
                      (tc/order-by [:type :a :b]))]
    ds-groups))


(-> (calc-aspect-stats aspects bars-ds [:type :a :b])
    (print-all))



