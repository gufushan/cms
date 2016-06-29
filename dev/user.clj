(ns user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [cms.core :refer [system-cms]]))

(set-init! #'system-cms)

(reset)
