(ns ip-geolocation.quova-ip-format-test
	(:use [clojure.test :only [deftest is are]]))

(deftest octetIp->longIp-test
	(are [octetIp longIp] (= (octetIp->longIp octetIp) longIp)
		"202.186.13.4" 3401190660
		"0.0.0.37"		 37
		"1"						 16777216)
	(is (thrown? NumberFormatException (octetIp->longIp ""))))

