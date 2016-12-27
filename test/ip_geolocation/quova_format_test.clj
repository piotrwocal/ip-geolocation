(ns ip-geolocation.quova-format-test
	(:use [clojure.test :only [deftest is are]])
	(:require [ip-geolocation.quova-format :refer :all]))


(deftest octetIp->longIp-test
	(are [octetIp longIp] (= (octetIp->longIp octetIp) longIp)
		"202.186.13.4" 3401190660
		"0.0.0.37"		 37
		"1"						 16777216)
	(is (thrown? NumberFormatException (octetIp->longIp ""))))


(deftest longIp->octetIp-test
  (are [longIp octetIp] (= (longIp->octetIp longIp) octetIp)
    3401190660   "202.186.13.4"
    37           "0.0.0.37"
    16777216     "1.0.0.0"))

