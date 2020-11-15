(ns adstxt.routes.ui
  (:require [adstxt.routes.home :refer [home-routes]]
            [adstxt.routes.domains :refer [domains-routes]]
            [adstxt.routes.records :refer [records-routes]]
            [adstxt.routes.about :refer [about-routes]]
            [compojure.core :refer [routes]]))


(def ui-routes
  (routes
   #'home-routes
   #'domains-routes
   #'records-routes
   #'about-routes))








