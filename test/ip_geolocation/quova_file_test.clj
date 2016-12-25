(ns ip-geolocation.quova-file-test
	(:use [clojure.test :only [deftest is are]])
	(:require [ip-geolocation.quova-file :refer :all])
	(:require [clojure.java.io :as io]))

; https://github.com/jonase/mlx/wiki/Analyzing-Stack-Overflow-data
; http://www.metasoarous.com/presenting-semantic-csv/

(def quova-data-snippet
	["16777216|16777471|24|oceania|australia|au|99|unknown|queensland|25|brisbane|22|4000|0|10|-27.4709|153.0235|0|0|0|tx|high|fixed|N|0|0|0|1313695448|0"
		"16777472|16778239|24|asia|china|cn|99|unknown|fujian|95|fuzhou|80|350000|0|8|26.06277803|119.2900022|0|0|0|unknown|unknown|none|N|0|0|0|1307256208|0"
		"16778240|16779007|24|oceania|australia|au|99|unknown|victoria|74|melbourne|50|3000|0|10|-37.8132|144.963|0|0|0|unknown|unknown|none|N|56203|7482486|440|1312270711|1312378472"
		"16779008|16779263|24|oceania|australia|au|99|unknown|victoria|74|melbourne|50|3000|0|10|-37.8132|144.963|0|0|0|unknown|unknown|none|N|38803|7482486|440|1312270711|1307550140"
		"16779264|16780031|24|asia|china|cn|86|unknown|guangdong|73|guangzhou|61|510000|0|8|23.12472086|113.2386109|0|0|0|unknown|unknown|none|N|0|0|0|1307256210|0"
		"16780032|16780799|24|asia|china|cn|99|unknown|guangdong|95|guangzhou|80|510000|0|8|23.12472086|113.2386109|0|0|0|unknown|unknown|none|N|0|0|0|1307256210|0"
		"16780800|16781311|24|asia|china|cn|86|unknown|guangdong|73|guangzhou|61|510000|0|8|23.12472086|113.2386109|0|0|0|unknown|unknown|none|N|0|0|0|1307256210|0"
		"16781312|16785407|24|asia|japan|jp|86|unknown|tokyo|73|tokyo|61|162-0843|0|9|35.6895|139.6917|0|0|0|unknown|unknown|none|N|0|0|0|1307635324|0"
		"16785408|16786943|24|asia|china|cn|86|unknown|guangdong|73|guangzhou|61|510000|0|8|23.12472086|113.2386109|0|0|0|unknown|unknown|none|N|0|0|0|1307256210|0"
		"16786944|16787199|24|asia|china|cn|99|unknown|guangdong|95|guangzhou|80|510000|0|8|23.12472086|113.2386109|0|0|0|tx|high|fixed|N|0|0|0|1307256210|0"])

(def quova-data-snippet-str
	(apply str (interpose "\n" quova-data-snippet)))


(def quova-fraction-line
	"33953792|33955839|24|europe|france|fr|99|nouvelle-aquitaine|gironde|98|bordeaux|98|33000|0|1|44.8378|-.579|0|0|0|dsl|medium|fixed|N|3215|147886|75|1311845603|1313276323")


(deftest parse-line-test
	(is (= (map parse-line (take 5 quova-data-snippet))
				 ['(16777216 16777471 -27 153)
					'(16777472 16778239 26 119)
					'(16778240 16779007 -37 144)
					'(16779008 16779263 -37 144)
					'(16779264 16780031 23 113)])))


(deftest parse-fraction-line-test
	(is (= [33953792 33955839 44 0] (parse-line quova-fraction-line))))


(deftest is-mergable?-test
	(are [result first second]
		(= result (is-mergable? first second))
	 		true [1 2 37 73] [3 4 37 73]
	 		false [1 2 38 73] [3 4 37 73]
	 		false [1 1 37 73] [3 4 37 73]
	 		false [1 3 37 73] [3 4 37 73]))


(def compacted-txt
	(let [rdr (io/reader (io/input-stream (.getBytes quova-data-snippet-str)))
				wrt (java.io.StringWriter.)]
		(compact rdr wrt)
		(.toString wrt)))


(deftest compact-test
	(let [rdr (io/reader (io/input-stream (.getBytes quova-data-snippet-str)))
				wrt (java.io.StringWriter.)]
		(compact rdr wrt)
		(is (= (.toString wrt)
"[16777216 16777471 -27 153]
[16777472 16778239 26 119]
[16778240 16779263 -37 144]
[16779264 16781311 23 113]
[16781312 16785407 35 139]
[16785408 16787199 23 113]\n"))))



