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

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def language-url "https://raw.githubusercontent.com/xfcjscn/ZhongYong/master/3ZW/XiuDaoZhiWeiJiao/EECS/Language/Language.txt")

(def app-routes
  (routes
    (GET "/" [] (-> index default-layout html))
    (GET "/login" [] (-> login default-layout html))
    (GET "/language" [] (-> language-url slurp mp to-hiccup default-layout html))
    (GET "/md/:md-name" [md-name] (-> (str "md/" md-name) io/resource slurp mp to-hiccup default-layout html))))

(def app
  (-> app-routes
      wrap-webjars
      (wrap-defaults site-defaults)))