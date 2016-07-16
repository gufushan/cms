(ns cms.html
  (:require 
    [clojure.tools.logging :as log]
    [hiccup.page :refer :all]
    [hiccup.element :refer :all]
    [hiccup.form :refer :all] ))


(defn default-layout [content request]
  [:html
   [:head
    [:meta {:property "qc:admins" :content "1541247527630123056375"}]
    (include-css "/assets/bootstrap/css/bootstrap.css")
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
    (include-js "http://tajs.qq.com/stats?sId=56953083" "/assets/jquery/jquery.js" "/assets/tether/dist/js/tether.js" "/assets/bootstrap/js/bootstrap.js")
    ]])

(defn link-list [files request]
  [:ol
   (map (fn [file]
          (let [file-name (.getName file)
                file-name-view (str file-name (when (.isDirectory file) "/"))]
            [:li (link-to (str (-> request :uri) "/" file-name) file-name-view)])) 
        files)])
