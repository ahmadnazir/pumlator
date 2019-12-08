(ns an.pumlator.core
  (:require [an.pumlator.stream :as stream]
            [an.pumlator.parser :as p])
  (:gen-class))

(defn operation
  ;; Create a puml activity:
  ;;   <from> -> <to> : <action>
  ;;   activate <to>
  [operation from to action]
  (format "%s -> %s : %s\n%s %s\n" from to action
          (case operation
            :request "activate"
            :respond "deactivate"
            (throw (Exception. "Invalid operation"))
            )
          (case operation
            :request to
            :respond from
            (throw (Exception. "Invalid operation")))))

(defn reducer
  ;; if stack is empty OR :from matches previous :to
  ;; - push into stack and continue
  ;; - update the acc (activate)
  ;; else
  ;; - empty stack until it does match
  ;; - update the acc (deactivate)
  [{puml :puml [prev & stack' :as stack] :stack} current]
  (if (or (nil? prev)
          (= (prev :to) (current :from)))
    {:puml (str puml
                (operation :request (current :from) (current :to) (current :action)))
     :stack (conj stack (assoc current :action "?"))
     }
    ;; else
    {:puml (str puml
                (operation :respond (prev :to) (current :from) "?")
                (operation :request (current :from) (current :to) (current :action)))
     :stack (conj stack' (assoc current :action "?"))
     }
    ))

(assoc {} :line "alskdf")

(defn pumlate
  "Generate plantuml from the expression"
  [expression]
  (let [{puml :puml stack :stack} (reduce reducer {} (p/evaluate expression))]
    (str puml
         ;; cleanup stack
         (reduce (fn [acc {from :from to :to action :action}] (str acc (operation :respond to from action))) "" stack))
    ))

(defn parse-args [args]
  (println "Arguments are ignored: " args))

(defn parse-line [line]
  (str " :: " line))

(defn print-line [line]
  (->> line
       parse-line
       println))

(defn -main [& args]
  (let [index (parse-args args)]
    (stream/mapper *in* print-line)))
