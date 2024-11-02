(ns astro.aspect
  (:require
   [clojure.data :refer [diff]]
   [clojure.pprint :refer [print-table]]
   [tick.core :as t]
   [astro]))

(defn- subsets [n items]
  (cond
    (= n 0) '(())
    (empty? items) '()
    :else (concat (map
                   #(cons (first items) %)
                   (subsets (dec n) (rest items)))
                  (subsets n (rest items)))))

(defn aspect [angel]
  (let [angel (Math/abs angel)]
    (cond
      (and (< angel 3)  (> angel -1)) :conjunction
      (and (< angel 32) (> angel 28)) :thirty
      (and (< angel 62) (> angel 58)) :sextile
      (and (< angel 92) (> angel 88)) :square
      (and (< angel 122) (> angel 118)) :trine
      (and (< angel 182) (> angel 178)) :opposition
      (and (< angel 47) (> angel 43)) :semi-square ; :45 degree : semi-square
      (and (< angel 74) (> angel 70)) :quintile ; :72 degree : quintile
      (and (< angel 137) (> angel 133)) :sesquiquadrate ; 135" 
      (and (< angel 146) (> angel 142)) :biquentile ; 144" 
      (and (< angel 152) (> angel 148)) :quincunx ; 150" 
      (and (< angel 53) (> angel 49)) :septile ; 51.43" 
      (and (< angel 156) (> angel 152)) :triseptile ; 154.29" 
      (and (< angel 42) (> angel 38)) :novile ; 40" 
      (and (< angel 82) (> angel 78)) :binovile ; 80" 
      ;(and (< angel 148) (> angel 152)) :parallel ; : degree 
      )))

(defn aspect-for [points]
  (fn [[a b]]
    (let [adeg (get-in points [a :lon])
          bdeg (get-in points [b :lon])
          angel (- adeg bdeg)
          c (aspect angel)]
      (when c
        {:type c :a a :b b}))))

(defn find-aspects [res]
  (let [points (:points res)
        planets (keys points)
        combinations (subsets 2 planets)
        calc-aspect (aspect-for points)]
    (map calc-aspect combinations)))

(defn find-current-aspects [res]
  (->> (find-aspects res)
       (remove nil?)))

(defn aspects-for-date [dt]
  (->> (astro/calc-date dt)
       find-current-aspects
       (assoc {:date dt} :aspects)))

;; date range

(defn add-aspect [date new-aspect]
  [new-aspect {:start date :end date}])

(defn update-aspect [date existing-aspect]
  [existing-aspect {:end date}])

(defn map->vec [old-map]
  ;(println "old-map: " old-map)
  (map (fn [[k v]]
         ; (println "processing: k: " k " v: " v)
         (merge k v))
       old-map))

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(defn add-date [v-aspect-durations {:keys [date aspects]}]
  (let [aspect-duration-map-old @v-aspect-durations
        ;_ (println "aspect-duration-map-old: " aspect-duration-map-old)
        aspect-duration-keys (keys aspect-duration-map-old)
        aspect-duration-keys (if aspect-duration-keys aspect-duration-keys '())
        ;_ (println "aspect-old: " aspect-duration-keys)
        ;_ (println "aspect-new: " aspects)
        [old new same] (diff (into #{} aspect-duration-keys) (into #{} aspects))
        ;; NEW         
        ;_ (println "new: " new)
        new-data (if new
                   (into {} (map (partial add-aspect date) new))
                   {})
        ;_ (println "new data: " new-data)
        ; SAME
        ;_ (println "same: " same)
        same-old (select-keys aspect-duration-map-old same)
        ;_ (println "same-old: " same-old)
        same-data (if same
                    (into {} (map (partial update-aspect date) same))
                    {})
        ;_ (println "same data: " same-data)
        aspect-duration-map (merge new-data (deep-merge same-old same-data))
        ;_  (println "current aspect duration map: " aspect-duration-map)
        ; OLD 
        ;_ (println "old: " old)
        old-map (select-keys aspect-duration-map-old old)
        old-vec (map->vec old-map)
        ;_ (println "old vec: " old-vec)
        ]
    ; old -> add event
    ; new -> add to accumulator with start date
    ; same -> add end date
    (vreset! v-aspect-durations aspect-duration-map)
    old-vec))

(defn xf-aspect-duration []
  (fn [xf]
    (let [v-aspect-durations (volatile! {})]
      (fn
        ([] (xf))
        ([result]
         (println "finished result: " @v-aspect-durations)
         (xf result))
        ([result {:keys [date aspects] :as input}]
         (let [finished-aspects (add-date v-aspect-durations input)]
            ;(println "finished aspects: " finished-aspects)
           (xf result finished-aspects)
           #_(doall
              (for [a finished-aspects]
                (do
                ;(println "finished aspect: " a "result: " result)
                  (xf result a))))))))))

(defn date-range [date-begin date-end]
  (let [date-begin (t/inst date-begin)
        date-end (t/inst date-end)
        d (t/new-duration 1 :hours)]
    (loop [dt date-begin
           r [dt]]
      (let [dt-next (t/>> dt d)]
        (if (t/< dt-next date-end)
          (recur dt-next (conj r dt-next))
          r)))))

(defn calc-aspect-durations [dates]
  (flatten (transduce (xf-aspect-duration) conj (map aspects-for-date dates))))

#_(defn find-aspects-duplicates [res]
    (let [points (:points res)
          planets (keys points)]
      (for [a planets
            b planets]
        (when-not (= a b)
          (let [adeg (get-in points [a :lon])
                bdeg (get-in points [b :lon])
                angel (- adeg bdeg)
                c (aspect angel)]
            (when c
              {:type c :a a :b b}))))))

;; print

(defn print-aspects [{:keys [date aspects]}]
  (print "\r\nAspects @ " date)
  (print-table aspects))

(defn print-aspects-window [aspects]
  (print-table [:type :a :b :start :end] aspects))

;; filter

(defn p-before-now [n]
  (fn [{:keys [end] :as a}]
    (t/<= end n)))

(defn p-select-has-window [{:keys [start end] :as a}]
  (t/< start end))

(defn select-aspects-until-now [aspects]
  (->> aspects
       (filter p-select-has-window)
       (filter (p-before-now (t/instant)))))

(defn moon-aspect? [{:keys [a b]}]
  (or (= a :Moon) (= b :Moon)))

(defn remove-moon-aspects [aspects]
  (remove moon-aspect? aspects))

(defn aspect-type? [T A B]
  (fn [{:keys [type a b]}]
    (and (= type T) (= a A) (= b B))))