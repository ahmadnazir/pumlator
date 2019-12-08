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
         :stack [{:from "a" :to "b" :action "?"}]}
        (sut/reducer {:puml "" :stack nil} (first (p/evaluate "a -> b : getId")))
        ))))

(deftest process-nested-operation
  (testing "a->b [b->c]"
    (is
     (= {:puml (join ["b -> c : getId"
                      "activate c"])
         :stack [{:from "b" :to "c" :action "?"} {:from "a" :to "b" :action "?"}]}
        (sut/reducer {:puml "" :stack (seq [{:from "a" :to "b" :action "?"}])} (first (p/evaluate "b -> c : getId")))
        ))))

(deftest process-multiple-operations
  (testing "a->b [a->c]"
    (is
     (= {:puml (join ["b -> a : ?"
                      "deactivate b"
                      "a -> c : getId"
                      "activate c"])
         :stack [{:from "a" :to "c" :action "?"}]
         }
        (sut/reducer {:puml "" :stack (seq [{:from "a" :to "b" :action "?"}])} (first (p/evaluate "a -> c : getId")))
        ))))

(deftest process-comment
  (testing "a->b [a->c]"
    (is
     (= {:puml (join ["note over b : this is a comment"])
         :stack [{:from "a" :to "b" :action "?"}]
         }
        (sut/reducer {:puml "" :stack (seq [{:from "a" :to "b" :action "?"}])} (first (p/evaluate "# this is a comment")))
        ))))

(deftest split-stack
  (testing "Splitting a stack"
    (is
     (= [[{:from "d"}]
         [{:from "c"} {:from "b"} {:from "a"}]]
        (sut/split-stack (seq [{:from "d"} {:from "c"} {:from "b"} {:from "a"}]) {:from "c"})
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
