(ns cms.data
  (:require 
    [hiccup.form :refer :all]))


(def schema 
  [{:db/id #db/id[:db.part/db]
    :db/ident :cms/comment
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/fulltext true
    :db/doc "Comments for article"
    :db.install/_attribute :db.part/db}])
