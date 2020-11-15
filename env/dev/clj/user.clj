(ns user
  (:require [luminus-migrations.core :as migrations]
            [adstxt.config :refer [env]]
            [mount.core :as mount]
            adstxt.core))

(defn start []
  (mount/start-without #'adstxt.core/repl-server))

(defn stop []
  (mount/stop-except #'adstxt.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))


