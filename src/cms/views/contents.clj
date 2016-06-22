(ns cms.views.contents
  (:require [hiccup.element :refer :all]
            [hiccup.form :refer :all]))

(defn link-list [files]
  (map (fn [file]
         (let [file-name (.getName file)]
           [:li (link-to (str "md/" file-name) file-name)])) 
       files))