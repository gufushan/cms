(ns cms.views.contents
  (:require [hiccup.element :refer :all]
            [hiccup.form :refer :all]))

(def login
  [:form "aaa"])

(def index 
  [:section 
   [:h2 "This is a knowlege sharing site."]
   [:li (link-to "md/function.md" "function")]
   ])
