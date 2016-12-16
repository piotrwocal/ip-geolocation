(ns ip-geolocation.quova-ip-format
	(:require [clojure.string :as str]))


(def octet-ratios [16777216 65536 256 1])


(defn octetIp->longIp [octetIp]
	(let [octet-parts (map #(Integer/valueOf %) (str/split octetIp #"\."))]
		(apply + (map * octet-parts octet-ratios))))


(defn longIp->octetIp [longIp]
	(->> (map #(mod (quot longIp %) 256) octet-ratios)
			 (map str)
			 (interpose ".")
			 (apply str)))



