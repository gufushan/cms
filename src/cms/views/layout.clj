(ns cms.views.layout
  (:require [hiccup.page :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :refer :all] ))


(defn default-layout [content]
  [:html
   [:head
    (include-css "/assets/bootstrap/css/bootstrap.css")
    (include-js "http://tajs.qq.com/stats?sId=56953083" "/assets/jquery/jquery.js" "/assets/tether/dist/js/tether.js" "/assets/bootstrap/js/bootstrap.js")]
   [:body
    content
    ]])