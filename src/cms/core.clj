(ns cms.core
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]
            [clojure.core.server :refer [start-server stop-server]]
            [ring.util.response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            ;; prone.middleware have abundant info than ring.middleware.stacktrace
            [prone.middleware :as prone]
            [prone.debug :refer [debug]]
            [ring.logger :as logger]
            [compojure.core :refer :all]
            [compojure.route :as route]
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

(defonce all-the-sessions (atom {}))

(defn app-routes-factory [qq-oauth2]
  (routes
    (GET "/" request (response/redirect "/md"))
    (GET "/md*" request (let [file (-> (:uri request) (subs 1) io/resource .getFile io/file)]
                           (if (.isDirectory file)
                             (-> file .listFiles (link-list request) (default-layout request) html)
                             (-> file slurp mp to-hiccup (default-layout request) html))))
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
         #_(debug)
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
                                        (re-find #"(?<=\"openid\":\").*(?=\")" $$)
                                        (clojure.string/replace $$ "-" "")))
               (assoc $ :qq-user-info (as-> $ $$
                                            (:oauth-client $$)
                                            ($$ {:method :get
                                                 :url (:qq-user-info-uri qq-oauth2)
                                                 :query-params {
                                                                 "oauth_consumer_key" (:client-id qq-oauth2)
                                                                 "openid" (:identity $)
                                                                 }
                                                 :as :json})))
               (assoc (response/redirect (-> $ :history rseq (nth 1))) :session $)))
    (route/not-found "Page Not Found")))


(defn wrap-history [handler & {:keys [max-size ignore-4xx ignore-5xx]}]
  (fn [request]
    (let [response (handler request)
          max-size (or max-size 10)]
      (if (or (zero? max-size)
              (and ignore-4xx (> (:status response) 399) (< (:status response) 500))
              (and ignore-5xx (> (:status response) 499) (< (:status response) 600)))
        response
        (->  (if (contains? response :session)
               response
               (assoc response :session (:session request)))
             (update-in [:session :history]
                        (fn [history]
                          (as-> (or history []) $$
                                (if (or (< max-size 0) (> max-size (count $$)))
                                  $$
                                  (subvec $$ 1 max-size))
                                (conj $$ (:uri request))))))))))


(defn app-factory [app-routes]
  (let [auth-backend (backends/session)]
    (-> app-routes
        (wrap-history :max-size 5 :ignore-4xx true :ignore-5xx true)
        (wrap-authorization auth-backend)
        (wrap-authentication auth-backend)
        prone/wrap-exceptions
        (logger/wrap-with-logger {:printer :no-color})
        wrap-webjars
        (wrap-defaults (-> site-defaults
                           (assoc-in [:security :frame-options] {:allow-from "www.sharkxu.com"})
                           (assoc-in [:session :store] (ring.middleware.session.memory/memory-store all-the-sessions))))
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

#_(-main)
#_(reset)

#_(add-dependencies :coordinates '[[xxxxxxx "1.2.3"]]
                    :repositories (merge cemerick.pomegranate.aether/maven-central
                                         {"clojars" "http://clojars.org/repo"}))
