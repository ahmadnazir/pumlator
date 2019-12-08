(ns ahmadnazir.pumlator.parser-test
  (:require [ahmadnazir.pumlator.parser :as sut]
            [clojure.test :refer :all]
            [ahmadnazir.pumlator.parser :as p]))


(deftest parse-one
  (testing "Parse one expression"
    (is
     (= [{:from "a" :to "b" :action "getId"}]
        (p/evaluate "a -> b : getId")
        ))))

(deftest parse-multiple
  (testing "Parse multiple expressions"
    (is
     (= [{:from "a" :to "b" :action "getId"}
         {:from "b" :to "c" :action "getX"}]
        (p/evaluate "a -> b : getId\nb -> c : getX")
        ))))

(deftest parse-action-with-multiple-words
  (testing "Parse an action with multiple words"
    (is
     (= [{:from "a" :to "b" :action "Do something"}]
        (p/evaluate "a -> b : Do something")
        ))))

(deftest parse-comment
  (testing "Parse a comment"
    (is
     (= [{:comment "this is a comment"}]
        (p/evaluate "# this is a comment")
        ))))
