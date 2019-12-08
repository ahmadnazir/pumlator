(ns an.pumlator.core-test
  (:require [an.pumlator.core :as sut]
            [clojure.test :refer :all]
            [clojure.string :as s]
            [an.pumlator.parser :as p]
            ))

(defn join
  [lines]
  (s/join "\n" (conj lines ""))
  )

(deftest process-single-operation
  (testing "a->b"
    (is
     (= {:puml (join ["a -> b : getId"
                      "activate b"])
         :stack ["b"]}
        (sut/reducer {:puml "" :stack nil} (first (p/evaluate "a -> b : getId")))
        ))))

(deftest process-nested-operation
  (testing "a->b [b->c]"
    (is
     (= {:puml (join ["b -> c : getId"
                      "activate c"])
         :stack ["c" "b"]}
        (sut/reducer {:puml "" :stack (seq ["b"])} (first (p/evaluate "b -> c : getId")))
        ))))

(deftest process-multiple-operations
  (testing "a->b [a->c]"
    (is
     (= {:puml (join ["b -> a : ?"
                      "deactivate b"
                      "a -> c : getId"
                      "activate c"])
         :stack ["a"]}
        (sut/reducer {:puml "" :stack (seq ["b"])} (first (p/evaluate "a -> c : getId")))
        ))))

(deftest pumlate-single-operation
  (testing "a->b"
    (is
     (= (join ["a -> b : getId"
               "activate b"
               "b -> a : ?"
               "deactivate b"
               ])
        (sut/pumlate "a -> b : getId")
        ))))