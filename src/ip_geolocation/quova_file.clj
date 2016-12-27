(ns ip-geolocation.quova-file
	(:require [clojure.java.io :as io]
						[ip-geolocation.quova-format :refer :all]))


(def QUOVA_FILE_NAME "../EDITION_Gold_2016-12-08_v815.dat")
(def QUOVA_COMPACTED_FILE_NAME "../quova-compacted.dat")


(defn parse-value [value]
	(let [parsed (read-string value)]
		(if (symbol? parsed) 0 (long parsed))))


(defn parse-line [line]
	(->> (-> (clojure.string/split line #"\|")
					 (mapv [0 1 15 16]))
			 (mapv parse-value)))


(defn is-mergable? [first-parsed second-parsed]
	(when-not (some empty? [first-parsed second-parsed])
		(let [first-end  (get first-parsed 1)
					second-start (get second-parsed 0)
					first-geo (mapv first-parsed [2 3])
					second-geo (mapv second-parsed [2 3])]
			(and (= first-geo second-geo)
					 (= (inc first-end) second-start)))))


(defn merge-parsed[[first-start _ first-lat first-long][_ second-end &rest]]
	[first-start second-end first-lat first-long])


(defn compact [rdr wrt]
	(with-open [rdr rdr, wrt wrt]
		(let [lines (line-seq rdr)]
			(loop [current (parse-line (first lines)), lines-left (rest lines)]
				(if (seq lines-left)
					(let [next (parse-line (first lines-left))]
						(if (is-mergable? current next)
							(recur (merge-parsed current next) (rest lines-left))
							(do (.write wrt (str (pr-str current) "\n"))
									(recur next (rest lines-left)))))
						(.write wrt (str (pr-str current) "\n" )))))))


(defn compact-original-file []
	(compact (io/reader QUOVA_FILE_NAME) (io/writer QUOVA_COMPACTED_FILE_NAME)))


(defn load-compacted-arr [rdr]
	(with-open [rdr rdr]
		(->> rdr
			 line-seq
			 (map read-string)
			 (map (partial into-array Long/TYPE))
			 into-array)))


(def compacted-arr
	(load-compacted-arr (io/reader "../QUOVA-10-compacted.dat")))


(defn in-range? [value row-data]
	(<= (aget row-data 0) value (aget row-data 1)))


(defn find-data
	([data to-find]
	  (find-data data to-find 0 (dec (alength data))))
	([data to-find left right]
		(let [mid-idx (quot (+ right left) 2)
					mid-data (aget data mid-idx)
					mid-value (aget mid-data 0)]
			(if (>= right left)
				(cond
					(in-range? to-find mid-data) mid-data
					(> to-find mid-value) (recur data to-find (inc mid-idx) right)
					(< to-find mid-value)	(recur data to-find left (dec mid-idx)))
				(let [lo-idx (Math/max (dec mid-idx) 0)
							hi-idx (Math/min (inc mid-idx) (dec (alength data)))]
					(cond
						(in-range? to-find (aget data lo-idx)) (aget data lo-idx)
						(in-range? to-find (aget data hi-idx)) (aget data hi-idx)))))))


(defn octetIp->geo [data octetIp]
	(some->> (octetIp->longIp octetIp)
			 (find-data data)
			 vec
			 (#(subvec % 2))))


;(pprint (octetIp->geo qf/compacted-arr  "1.0.0.0"))