(ns ads-txt-reporter.routes.home
  (:require [ads-txt-reporter.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ads-txt-reporter.db.core :as db]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [struct.core :as st]
            [clojurewerkz.urly.core :refer [url-like as-map]]))

(def name-schema
  [[:name
    st/required
    st/string
    ]])

(defn validate-name [params]
  (first (st/validate params name-schema)))


;; ----------------------------------------------------------------------------------------------------
;; These routines can and should be pulled out from ads-txt-crawler
;; Added here for a first pass
(defn strip-www
  "Remove preceeding www. from url"
  [domain]
  (if domain
    (let [[a b] (clojure.string/split domain #"^www.")]
      (if b
        b
        a))))

(defn hostname
  "Parse url into components and return hostname"
  [url]
  (if-let [hostname (:host (as-map (url-like url)))]
    (strip-www hostname)))


(defn save-domain! [{:keys [params]}]
  (if-let [hostname (hostname (:name params))]
    (let [params (assoc params :name hostname)]
      (if-let [errors (validate-name params)]
        (-> (response/found "/domains")
            (assoc :flash (assoc params :errors errors)))
        (do
          (try
            (db/save-domain! (assoc params :timestamp (java.sql.Timestamp. (.getTime (java.util.Date.)))))
            (catch java.lang.Exception e
              ;; ignore duplicate entries
              ))
          (response/found "/domains"))
        )
      )
    (response/found "/domains")
    )
  )


(defn domains-page [{:keys [flash]}]
  (println (select-keys flash [:name :errors :message]))
  (layout/render
   "domains.html"
   (merge {:domains (db/get-domains)}
          (select-keys flash [:name :errors :message]))))

(defn home-page []
  (layout/render "home.html"))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/domains" request (domains-page request))
  (POST "/domains" request (save-domain! request))
  (GET "/about" [] (about-page)))
