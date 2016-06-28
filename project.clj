(defproject cms "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.385"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-devel "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-webjars "0.1.1" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [compojure "1.5.0"]
                 [hiccup "1.0.5"]

                 [org.webjars.bower/tether "1.3.2"]
                 [org.webjars/bootstrap "4.0.0-alpha.2"]
                 [endophile "0.1.2"]
                 [buddy/buddy-auth "1.1.0"]
                 [oauth-clj "0.1.15"]
                 [org.danielsz/system "0.3.0"]

                 [lein-light-nrepl "0.3.3"]

                 [ch.qos.logback/logback-classic "1.1.7"]]
  :main cms.core
  :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]})
