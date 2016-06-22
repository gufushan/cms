(ns cms.core
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [compojure.core :refer :all]
            [hiccup.core :refer [html]]
            [endophile.core :refer [mp]]
            [endophile.hiccup :refer [to-hiccup]]
            [cms.views.layout :refer :all]
            [cms.views.contents :refer :all]

            [clojure.java.io :as io]))

  
(def app-routes
  (routes
    (GET "/" [] (-> "md" io/resource .getFile io/file file-seq rest link-list default-layout html))
    (GET "/md/:md-name" [md-name] (-> (str "md/" md-name) io/resource slurp mp to-hiccup default-layout html))))

(def app
  (-> app-routes
      wrap-webjars
      (wrap-defaults (assoc-in site-defaults [:security :frame-options] {:allow-from "www.sharkxu.com"}))))