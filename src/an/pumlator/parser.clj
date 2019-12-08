(ns an.pumlator.parser
  (:require [instaparse.core :as insta]
            ;; [clojure.string :as s]
            [clojure.core.match :refer [match]])
  )

(def parse (->> (System/getProperty "user.dir")
                (format "%s/grammar.bnf" )
                slurp
                insta/parser
                ))

(defn parse-tree
  "Parse the expression"
  [expression]
  (let [parsed (parse expression)
        error (insta/failure? parsed)
        ]
    (if error {:error (insta/get-failure parsed)}
        parsed
        )))


(defn operation-ast
  "Convert operation parse tree to ast"
  [operation]
  (match operation
         [:OPERATION [:FROM [:string from]] [:TO [:string to]] [:ACTION [:string action]]]
         {:from from
          :to to
          :action action}
         :else (throw (Exception. (format "Unable to match %s" operation)))
         )
  )

(defn ast
  "Convert parse tree to abstract syntax tree"
  [parsed]
  (match parsed
         [:OPERATIONS operation & operations] {:ast (map operation-ast (cons operation operations))}
         :else                    {:error (format "Matching rules incomplete for: %s" parsed)}
         ))

(defn evaluate
  [expression]
  (let [pt (parse-tree expression)]
    (cond (:error pt) (:error pt)
          :else (:ast (ast pt)))))


