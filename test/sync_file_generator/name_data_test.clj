(ns sync-file-generator.name-data-test
  (:require [clojure.test :refer :all]
            [sync-file-generator.name-data :refer :all])
  (:import (java.util UUID)))

(defn random-weighted-map []
  (let [random-kv (fn [] [(str (UUID/randomUUID)) {:weight (inc (rand-int 100))}])
        kv-seq (repeatedly random-kv)]
    (into {} (take (inc (rand-int 20)) kv-seq))))

(defn sum-weights [weighted-map]
  (let [get-weight (fn [[k v]] (:weight v))]
    (reduce + (map get-weight weighted-map))))

(defn close-enough?
  ([n target]
   (close-enough? n target 0.00000001))
  ([n target epsilon]
   (< (Math/abs (- n target))
      epsilon)))

(deftest normalized-weights-add-to-one
  (doseq [n (range 10000)]
    (let [weighted-map (normalize-weights (random-weighted-map))]
      (is (close-enough? (sum-weights weighted-map) 1.0)))))

(deftest frequencies-of-selection-should-match-weights
  (let [num-trials 100000
        weighted-map (normalize-weights (random-weighted-map))
        random-keys (repeatedly num-trials #(rand-element-weighted weighted-map))
        counts (frequencies random-keys)]
    (doseq [[k v] weighted-map]
      (let [weight (:weight v)
            count (counts k)]
        (is (close-enough? weight (double (/ count num-trials)) 0.005))))))