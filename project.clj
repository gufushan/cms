(defproject cms "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-webjars "0.1.1"]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]
                 [org.webjars.bower/tether "1.3.2"]
                 [org.webjars/bootstrap "4.0.0-alpha.2"]
                 [endophile "0.1.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler cms.core/app})
