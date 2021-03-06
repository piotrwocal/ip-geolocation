(ns ip-geolocation.quova-format
	(:require [clojure.string :as str]))


(def octet-ratios [16777216 65536 256 1])


(defn octetIp->longIp [octetIp]
	"converts octet IP form like '234.234.123.32' to long representation"
	(let [octet-parts (map #(Integer/valueOf %) (str/split octetIp #"\."))]
		(apply + (map * octet-parts octet-ratios))))


(defn longIp->octetIp [longIp]
	"converts long IP representation to octet one"
	(->> (map #(mod (quot longIp %) 256) octet-ratios)
			 (map str)
			 (interpose ".")
			 (apply str)))