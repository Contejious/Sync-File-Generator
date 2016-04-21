(ns sync-file-generator.core
  (:require [sync-file-generator.name-data :as data :refer [rand-element rand-element-weighted]]
            [clojure.java.io :as io])
  (:import (java.util UUID)))

(def default-columns [:id :first :last :title :site :dept :email :active :policy-tech-active])

(def header-labels
  {:id "CustomID"
   :first "First Name"
   :last "Last Name"
   :title "Title"
   :site "Site"
   :dept "Dept"
   :email "Email"
   :active "Active"
   :policy-tech-active "PolicyTech Active"})

(defn random-record []
  (let [first-name (rand-element data/first-names)
        last-name (rand-element data/last-names)
        email (str first-name "." last-name "@foo.test")]
    {:id                 (str (UUID/randomUUID))
     :first              first-name
     :last               last-name
     :title              (rand-element data/titles)
     :site               (rand-element-weighted data/sites)
     :dept               (rand-element-weighted data/depts)
     :email              email
     :active             "y"
     :policy-tech-active "y"}))

(defn convert-to-tsv [columns record]
  (let [values (mapv record columns)]
    (clojure.string/join "\t" values)))

(def random-records (repeatedly random-record))

(defn flat-file-records [n]
  (concat [header-labels]
          (take n random-records)))

(defn create-sync-file
  ([file-path]
   (create-sync-file 20 file-path))
  ([line-count file-path]
   (create-sync-file default-columns line-count file-path))
  ([columns line-count file-path]
   (let [lines (map (partial convert-to-tsv columns) (flat-file-records line-count))]
    (with-open [wrtr (io/writer file-path)]
      (binding [*out* wrtr]
        (doseq [line lines]
          (println line)))))))


