(ns cms.html
  (:require [hiccup.page :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :refer :all] ))


(defn default-layout [content request]
  [:html
   [:head
    [:meta {:property "qc:admins" :content "1541247527630123056375"}]
    (include-css "/assets/bootstrap/css/bootstrap.css")
    ]
   [:body
    (link-to "/oauth2/entrypoint" "login")
    [:div
     (str "Welcome: " (-> request :identity))]
    content
    (include-js "http://tajs.qq.com/stats?sId=56953083" "/assets/jquery/jquery.js" "/assets/tether/dist/js/tether.js" "/assets/bootstrap/js/bootstrap.js")
    ]])

(defn link-list [files]
  [:ol
   (map (fn [file]
         (let [file-name (.getName file)]
           [:li (link-to (str "md/" file-name) file-name)])) 
       files)])
