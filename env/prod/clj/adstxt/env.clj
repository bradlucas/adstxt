(ns adstxt.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[adstxt started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[adstxt has shut down successfully]=-"))
   :middleware identity})
