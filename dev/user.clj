(ns user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [cemerick.pomegranate :refer [add-dependencies]]
            [cms.core :refer [system-cms]]))

(set-init! #'system-cms)

(reset)

#_(add-dependencies :coordinates '[[xxxxxxx "1.2.3"]]
                    :repositories (merge cemerick.pomegranate.aether/maven-central
                                         {"clojars" "http://clojars.org/repo"}))
