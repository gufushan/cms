(ns cms.core
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [ring.util.response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [compojure.core :refer :all]
            [hiccup.core :refer [html]]

            [endophile.core :refer [mp]]
            [endophile.hiccup :refer [to-hiccup]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends :as backends]
            [oauth.v2 :as oauth2]
            [com.stuartsierra.component :as component]
            [system.repl :refer [system set-init! start stop reset]]
            [system.components.jetty :refer [new-web-server]]

            [cms.html :refer :all]))

(def qq-oauth2
  {
   :authorization-uri "https://graph.qq.com/oauth2.0/authorize"
   :redirect-uri "http://www.gplatform.net:3000/oauth2/callback"
   :client-id "101155721"
   :client-secret "b548b17e4d77fcb20d5fab2481ea986d"
   :options {
             :scope ["get_user_info"]
             :response-type "code"
             }
   :access-token-uri "https://graph.qq.com/oauth2.0/token"

   :qq-openid-uri "https://graph.qq.com/oauth2.0/me"
   :qq-user-info-uri "https://graph.qq.com/user/get_user_info"
   })

(defn app-routes-factory [qq-oauth2]
  (routes
    (GET "/" [request] (-> "md" io/resource .getFile io/file file-seq rest link-list (default-layout request) html))
    (GET "/md/:md-name" [md-name] (-> (str "md/" md-name) io/resource slurp mp to-hiccup (default-layout nil) html))
    (GET "/postlogin/:path" [path] (str "This path need to login:" path))
    (GET "/oauth2/entrypoint" []
      (as-> qq-oauth2 $
            (into ((juxt :authorization-uri :client-id :redirect-uri) $) (->> $ :options (apply concat)))
            (apply oauth2/oauth-authorization-url $)
            (response/redirect $)
            (log/spy $)))
    (GET "/oauth2/callback" {{code :code} :params session :session}
      (as-> qq-oauth2 $
            (conj ((juxt :access-token-uri :client-id :client-secret) $) code (:redirect-uri $))
            (apply oauth2/oauth-access-token $)
            (:access-token $)
            (oauth2/oauth-client $)
            (assoc session :oauth-client $)
            (assoc $ :identity (as-> $ $$
                                     (:oauth-client $$)
                                     ($$ {:method :get
                                          :url (:qq-openid-uri qq-oauth2)})
                                     (str $$)
                                     (re-find #"(?<=\"openid\":\").*(?=\")" $$)))
            (assoc $ :qq-user-info (as-> $ $$
                                        (:oauth-client $$)
                                         ($$ {:method :get
                                              :url (:qq-user-info-uri qq-oauth2)
                                              :query-params {
                                                             "oauth_consumer_key" (:client-id qq-oauth2)
                                                             "openid" (:identity $)
                                                             }})))
            (assoc (response/redirect "/") :session $)
            (log/spy $)))))


(defn app-factory [app-routes]
  (let [auth-backend (backends/session)]
   (-> app-routes
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
      wrap-webjars
      (wrap-defaults (assoc-in site-defaults [:security :frame-options] {:allow-from "www.sharkxu.com"}))
      wrap-reload)))


;; this must be no parameter so it can be invoked by system/init
(defn system-cms []
  (as-> qq-oauth2 $
        (app-routes-factory $)
        (app-factory $)
        (component/system-map
          :web (new-web-server 3000 $))))

(defn -main
  "Start the application"
  []
  (set-init! #'system-cms)
  (start))
