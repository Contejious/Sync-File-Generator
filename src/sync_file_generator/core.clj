(ns sync-file-generator.core
  (:require [sync-file-generator.name-data :as data]
            [clojure.java.io :as io]))

(def headers ["CustomID"
              "First Name"
              "Last Name"
              "Title"
              "Site"
              "Dept"
              "Email"
              "Active"
              "PolicyTech Active"])

(defn rand-element [v]
  (when (vector? v)
    (v (rand-int (count v)))))

(defn random-record []
  (let [first-name (rand-element data/first-names)
        last-name (rand-element data/last-names)
        email (str first-name "." last-name "@foo.test")]
    {:id                 (str (java.util.UUID/randomUUID))
     :first              first-name
     :last               last-name
     :title              (rand-element data/titles)
     :site               (rand-element data/sites)
     :dept               (rand-element data/depts)
     :email              email
     :active             "y"
     :policy-tech-active "y"}))

(defn convert-to-file-format [record]
  (let [columns [:id :first :last :title :site :dept :email :active :policy-tech-active]
        values (mapv #(record %) columns)]
    (str (clojure.string/join "\t" values) "\n")))

(def random-records (repeatedly random-record))

(def random-records-tsv (map convert-to-file-format random-records))

(defn create-sync-file [&[line-count]]
  (with-open [wrtr (io/writer "/Users/Tej/GoogleDrive/code/sync-file-generator/output/sync-file.txt")]
    (doseq [record (take (or line-count 20) random-records-tsv)]
      (.write wrtr record))))