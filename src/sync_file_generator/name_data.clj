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
                   vals
                   (map :weight)
                   (reduce +))
        normalize (fn [weight] (double (/ weight total)))
        normalize-kv (fn [[key valm]] [key (update valm :weight normalize)])]
    (into {} (map normalize-kv m))))


(defn rand-element-weighted [m]
  (loop [selection (rand)
         kvs (seq m)]
    (let [[k v] (first kvs)
          weight (:weight v)]
      (if (or (< selection weight)
              (empty? (rest kvs)))
        k
        (recur (- selection weight) (rest kvs))))))

(def first-names (read-resource "CSV_Database_of_First_Names.tsv"))
(def last-names (read-resource "CSV_Database_of_Last_Names.tsv"))

(def titles
  (normalize-weights
    {"Vice President"       {:weight 1}
     "Director"             {:weight 3}
     "Assistant Director"   {:weight 5}
     "Supervisor"           {:weight 15}
     "Assistant"            {:weight 8}
     "Assistant Supervisor" {:weight 30}
     "Peon"                 {:weight 100}
     "Intern"               {:weight 15}}))

(def sites
  (normalize-weights
    {"Charlotte" {:weight 15}
     "LKO"       {:weight 30}
     "Rexburg"   {:weight 10}
     "Norcross"  {:weight 20}
     "London"    {:weight 5}
     "Remote"    {:weight 20}}))

(def depts
  (normalize-weights
    {"Sales"                 {:weight 10 :parents #{}}
     "IT"                    {:weight 10 :parents #{}}
     "Engineering"           {:weight 20 :parents #{"IT"}}
     "Customer Satisfaction" {:weight 30 :parents #{"Operations"}}
     "Accounting"            {:weight 10 :parents #{}}
     "Human Resources"       {:weight 10 :parents #{}}
     "Operations"            {:weight 10 :parents #{}}
     ""                      {:weight 10 :parents #{}}}))

odd?