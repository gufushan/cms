(ns cms.html
  (:require 
    [clojure.tools.logging :as log]
    [hiccup.page :refer :all]
    [hiccup.element :refer :all]
    [hiccup.form :refer :all] ))

(defn comments []
  [:section
   [:div {:class "ui rating" :data-max-rating "5"}]
   [:div {:class "ui labeled button"}
    [:div {:class "ui basic blue button"}
     [:i {:class "heart icon"}]
     "Like"]
    [:a {:class "ui basic red left pointing label"}
     "1,048"]]
   [:div {:class "ui comments"}
    [:div {:class "comment"}
     [:a {:class "avatar"}
      ]
     [:div {:class "content"}
      [:a {:class "author"}"Steve Jobes"]]
     [:div {:class "metadata"}
      [:div {:class "date"}"2 days ago"]]
     [:div {:class "text"}"Revolutionary!"]
     [:div {:class "actions"}
      [:a {:class "reply active"}"reply"]]
     [:form {:class "ui reply form" :action "/comment" :method "post"}
      [:div {:class "field"}
       [:textarea {:name "content"}]]
      [:div {:class "ui primary submit labeled icon button"}
       [:i {:class "icon edit"}]" Add Reply"]]]]])


(defn default-layout [content request]
  [:html
   [:head
    [:meta {:property "qc:admins" :content "1541247527630123056375"}]
    (include-css "/assets/semantic/dist/semantic.css")
    ]
   [:body
    (if (:identity request)
      [:div
       (str "Welcome: " (-> request :session :qq-user-info :nickname))
       #_(image (-> request :session :qq-user-info :figureurl))
       #_(image (-> request :session :qq-user-info :figureurl-2))
       (image (-> request :session :qq-user-info :figureurl-qq-1))
       #_(image (-> request :session :qq-user-info :figureurl-qq-2))
       (link-to "/remove-identity" "logout")]
      (link-to "/oauth2/entrypoint"
               (image "http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/img/bt_white_76X24.png")))

    content
    (comments)
    (include-js "/assets/jquery/jquery.js" "/assets/semantic/dist/semantic.js" "http://tajs.qq.com/stats?sId=56953083"
                "/cms.js")]])


(defn link-list [files request]
  [:ol
   (map (fn [file]
          (let [file-name (.getName file)
                file-name-view (str file-name (when (.isDirectory file) "/"))]
            [:li (link-to (str (-> request :uri) "/" file-name) file-name-view)])) 
        files)])
