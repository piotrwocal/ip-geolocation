(ns ip-geolocation.app
	(:require [ring.middleware.params :as ring]))

(defn service*
	[{:keys [uri params]}]
	{:body (format "3 newly requested %s with query %s" uri params)})

(def service (ring/wrap-params service*))

; to start server locally
; (use '[ring.adapter.jetty :only (run-jetty)])
; (def server (run-jetty #'service {:port 8080 :join? false}))