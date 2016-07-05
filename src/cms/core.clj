(ns cms.core
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [clojure.core.server :refer [start-server stop-server]]
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
            [cemerick.pomegranate :refer [add-dependencies]]

            [cms.html :refer :all]))

(def qq-oauth2
  {
    :authorization-uri "https://graph.qq.com/oauth2.0/authorize"
    :redirect-uri "http://www.sharkxu.com/oauth2/callback"
    :client-id "101326937"
    :client-secret "b9892d627e6ff3985eb25b32c1ade573"
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
    (GET "/" request (-> "md" io/resource .getFile io/file file-seq rest link-list (default-layout request) html))
    (GET "/md/:md-name" [md-name :as request] (-> (str "md/" md-name) io/resource slurp mp to-hiccup (default-layout request) html))
    (GET "/remove-identity" {session :session} 
         (as-> session $
               (dissoc $ :identity)
               (assoc (response/redirect "/") :session $)))
    (GET "/oauth2/entrypoint" []
         (as-> qq-oauth2 $
               (into ((juxt :authorization-uri :client-id :redirect-uri) $) (->> $ :options (apply concat)))
               (apply oauth2/oauth-authorization-url $)
               (response/redirect $)))
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
               (assoc (response/redirect (-> $ :history rseq (nth 1))) :session $)))))


(defn wrap-history [handler & [max-size]]
  (let [max-size (or max-size 10)]
    (fn [request]
      (if (zero? max-size)
        (handler request)
        (as-> 
          (handler (log/spy request)) $
          (log/spy $)
          (if (contains? $ :session) $ (assoc $ :session (:session request)))
          (update-in $ [:session :history]
                     (fn [history]
                       (as-> (or history []) $$
                             (if (or (< max-size 0) (> max-size (count $$)))
                               $$
                               (subvec $$ 1 max-size))
                             (conj $$ (:uri request))))))))))


(defn app-factory [app-routes]
  (let [auth-backend (backends/session)]
    (-> app-routes
        (wrap-history 5)
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
          :web (new-web-server 80 $))))


(defn -main
  "Start the application"
  []
  (start-server {:name "repl" :port 5555 :accept 'clojure.core.server/repl :address "0.0.0.0" :server-daemon false})
  (set-init! #'system-cms)
  (start))

#_(add-dependencies :coordinates '[[xxxxxxx "1.2.3"]]
                    :repositories (merge cemerick.pomegranate.aether/maven-central
                                         {"clojars" "http://clojars.org/repo"}))
