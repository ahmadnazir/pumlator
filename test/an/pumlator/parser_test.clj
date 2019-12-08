(ns an.pumlator.parser-test
  (:require [an.pumlator.parser :as sut]
            [clojure.test :refer :all]
            [an.pumlator.parser :as p]))


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