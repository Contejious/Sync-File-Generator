(ns sync-file-generator.name-data
  (:require [clojure.java.io :as io]))

(defn read-resource [filename]
  (with-open [rdr (io/reader (io/file (io/resource filename)))]
    (vec (line-seq rdr))))

(defn rand-element [v]
  (when (vector? v)
    (v (rand-int (count v)))))

(defn normalize-weights [m]
  (let [total (->> m
                   (vals)
                   (reduce +))
        normalize (fn [[k v]] [k (double (/ v total))])]
    (into {} (map normalize m))))

(defn normalize-weights-map [m k]
  (let [total (->> m
                   vals
                   (map k)
                   (reduce +))
        normalize (fn [[key valm]] [key (update valm k (fn [weight] (double (/ weight total))))])]
    (into {} (map normalize m))))


(defn rand-element-weighted [m]
  (loop [selection (rand)
         kvs (seq m)]
    (let [[k v] (first kvs)]
      (if (or (< selection v)
              (empty? (rest kvs)))
        k
        (recur (- selection v) (rest kvs))))))

(def first-names (read-resource "CSV_Database_of_First_Names.csv"))
(def last-names (read-resource "CSV_Database_of_Last_Names.csv"))

(def titles
  [
   "CEO"
   "CTO"
   "CFO"
   "CIO"
   "CSO"
   "Director"
   "Supervisor"
   "Assistant"
   "Assistant Supervisor"
   "Peon"
   "Intern"
   ""
   ])

(def sites
  (normalize-weights
    {"Charlotte" 15
     "LKO"       30
     "Rexburg"   10
     "Norcross"  20
     "London"    5
     "Remote"    20}))

(def depts
  (normalize-weights-map
    {"Sales"                 {:weight 10 :parents #{}}
     "IT"                    {:weight 10 :parents #{}}
     "Engineering"           {:weight 20 :parents #{"IT"}}
     "Customer Satisfaction" {:weight 30 :parents #{"Operations"}}
     "Accounting"            {:weight 10 :parents #{}}
     "Human Resources"       {:weight 10 :parents #{}}
     "Operations"            {:weight 10 :parents #{}}
     ""                      {:weight 10 :parents #{}}}
    :weight))
