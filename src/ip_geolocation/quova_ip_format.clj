(ns ip-geolocation.quova-ip-format
	(:require [clojure.string :as str]))


(defn octetIp->longIp [octetIp]
	(let [octet-parts (map #(Integer/valueOf %) (str/split octetIp #"\."))
				octet-ratios [16777216 65536 256 1]]
		(apply + (map * octet-parts octet-ratios))))
