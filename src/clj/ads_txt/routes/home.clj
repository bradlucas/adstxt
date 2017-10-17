(ns ads-txt.routes.home
  (:require [ads-txt.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ads-txt.db.core :as db]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [struct.core :as st]
            [clojurewerkz.urly.core :refer [url-like as-map]]
            [ads-txt-crawler.process :as p]
            [ads-txt-crawler.domains :as d]))

(def name-schema
  [[:name
    st/required
    st/string
    ]])

(defn validate-name [params]
  (first (st/validate params name-schema)))

(defn hostname
  "Parse url into components and return hostname"
  [url]
  (if-let [hostname (:host (as-map (url-like (clojure.string/trim url))))]
    (d/strip-www hostname)))

(defn save-domain! [{:keys [params]}]
  (if-let [hostname (hostname (:name params))]
    (let [params (assoc params :name hostname)]
      (if-let [errors (validate-name params)]
        (-> (response/found "/domains")
            (assoc :flash (assoc params :errors errors)))
        (do
          (try
            (db/save-domain! params)
            (catch java.lang.Exception e
              ;; ignore duplicate entries
              ))
          hostname)))))

(defn crawl-domain-save [domain-name]
  (let [id (db/get-domain-id {:name domain-name})
        data (p/process domain-name)]
    (doseq [d data]
      (try
        (db/save-record! {:domain_id (:id id)
                          :exchange_domain (:exchange-domain d)
                          :seller_account_id (:account-id d)
                          :account_type (:account-type d)
                          :tag_id (:tag-id d)
                          :comment (:comment d)})
        (catch java.lang.Exception e
          ;; ignore duplicate entries
          )))))

(defn process-domain! [request]
  ;; save the domain to the databse first
  (if-let [hostname (save-domain! request)]
    ;; craw the domain
    (crawl-domain-save hostname))
    ;; show domains page
  (response/found "/domains"))

(defn domain-data-csv [report]
  (let [header ["name", "count"]
        data (map 
              (fn [line] 
                (vec (map 
                      (fn [item] (get line (keyword item))) header)
                     ))
              report)]
    (with-out-str (csv/write-csv *out* data))))

(defn download-domains-list-csv []
  (let [data (db/get-domains)]
    {:status 200
     :headers {"Content-Type" "text/csv; charset=utf-8"
               "Content-Length"      (str (count data))
               "Cache-Control"       "no-cache"
               "Content-Disposition" (str "attachment; filename=ads-txt-domains.csv")}
     :body (domain-data-csv data)}
    ))

(defn domains-page [{:keys [params]}]
  (if (:csv params)
    (download-domains-list-csv)
    (layout/render
     "domains.html"
     (merge {:domains (db/get-domains)}
            (select-keys params [:name :errors :message])))))

(defn records-data-csv [report]
  (let [header ["name", "exchange_domain", "seller_account_id", "account_type", "tag_id", "comment"]
        data (map 
              (fn [line] 
                (vec (map 
                      (fn [item] (get line (keyword item))) header)
                     ))
              report)]
    (with-out-str (csv/write-csv *out* data)))
  )


(defn download-records-list-csv [params]
  (let [data (if-let [id (:id params)]
               (db/get-records-for-domain {:id (Integer/parseInt id)})
               (db/get-records))
        ]
    {:status 200
     :headers {"Content-Type" "text/csv; charset=utf-8"
               "Content-Length"      (str (count data))
               "Cache-Control"       "no-cache"
               "Content-Disposition" (str "attachment; filename=ads-txt-records.csv")}
     :body (records-data-csv data)}
    )
)

(defn records-page [{:keys [params]}]
  (if (:csv params)
    (download-records-list-csv params)
    (layout/render
     "records.html"
     (merge {:records
             (if-let [id (:id params)]
               (db/get-records-for-domain {:id (Integer/parseInt id)})
               (db/get-records))
             :id (:id params)
             :domain-name
             (if-let [id (:id params)]
               (db/get-domain-name {:id (Integer/parseInt id)})
               )
             }
            (select-keys params [:name :errors :message])))))

(defn home-page []
  (layout/render
   "home.html"
   {:domains-count (db/get-domains-count)
    :records-count (db/get-records-count)}))

(defn about-page []
  (layout/render "about.html"))

;; (defn test []
;;   (try
;;       (db/save-record! {:domain_id 7
;;                         :exchange_domain "exchange-domain"
;;                         :seller_account_id "seller-account-id"
;;                         :account_type "account-type"
;;                         :tag_id "tagid"
;;                         :comment "this is a comment"})
;;       (catch java.lang.Exception e
;;         ;; ignore duplicates
;;         ))
;;   (response/found "/domains"))

;; (defn test2 []
;;   (let [domain "businessinsider.com"]
;;     (save-domain! {:params {:name domain}})
;;     (crawl-domain-save domain))
;;   (response/found "/domains"))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/domains" request (domains-page request))
  (POST "/domains" request (process-domain! request))
  (GET "/records" request (records-page request))
  (GET "/about" [] (about-page))
  ;; (GET "/test" [] (test))
  ;; (GET "/test2" [] (test2))
  )

