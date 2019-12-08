(ns an.pumlator.stream)

(defn read-all
  [reader]
  (do
    (clojure.string/join "\n" (line-seq (java.io.BufferedReader. reader)))))
