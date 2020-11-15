(ns adstxt.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [adstxt.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[adstxt started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[adstxt has shut down successfully]=-"))
   :middleware wrap-dev})
