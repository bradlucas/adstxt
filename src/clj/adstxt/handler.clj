(ns adstxt.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [adstxt.layout :refer [error-page]]
            ;; [adstxt.routes.home :refer [home-routes]]
            [adstxt.routes.api :refer [api-routes]]
            [adstxt.routes.ui :refer [ui-routes]]
            [compojure.route :as route]
            [adstxt.env :refer [defaults]]
            [mount.core :as mount]
            [adstxt.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'ui-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'api-routes
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
