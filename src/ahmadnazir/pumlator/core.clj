(ns ahmadnazir.pumlator.core
  (:require [ahmadnazir.pumlator.stream :as stream]
            [ahmadnazir.pumlator.parser :as p]
            [clojure.string :as s])
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

(defn note
  [note node]
  (format "note over %s : %s\n" node note))

(defn process-stack
  "Generate plantuml from the stack"
  [stack]
  (reduce (fn [acc {from :from to :to action :action}] (str acc (operation :respond to from action))) "" stack))

(defn split-stack
  "Split the stack ..."
  [stack current]
  (split-with (fn [x] (not (= (x :from) (current :from)))) stack))

(defn reducer
  ;; if stack is empty OR :from matches previous :to
  ;; - push into stack and continue
  ;; - update the acc (activate)
  ;; else
  ;; - empty stack until it does match
  ;; - update the acc (deactivate)
  [{puml :puml [prev & stack' :as stack] :stack} current]
  (cond
    ;; comment
    (:comment current)
    {:puml (str puml
                (if prev (note (current :comment) (prev :to)) ""))
     :stack stack
     }
    ;; Nested call
    (or (nil? prev) (= (prev :to) (current :from)))
    {:puml (str puml
                (operation :request (current :from) (current :to) (current :action))
                )
     :stack (conj stack (assoc current :action "?"))
     }
    ;; Same level call
    :else
    (let [[xs [y & ys]] (split-stack stack current)
          pending-stack (concat xs [y])
          new-stack ys
          ]
      {:puml (str puml
                  (process-stack pending-stack)
                  (operation :request (current :from) (current :to) (current :action))
                  )
       :stack (concat (rest new-stack) [(assoc current :action "?")])
       })
    ))

(defn pumlate
  "Generate plantuml from the expression"
  [expression]
  (let [{puml :puml stack :stack} (reduce reducer {} (p/evaluate expression))]
    (str puml (process-stack stack))
    ))

(defn indent
  [puml]
  (str "@startuml\n\n    " (s/replace puml "\n" "\n    ")  "\n    @enduml"))

(defn parse-args [args]
  args
  ;; ignoring arguments
  )

(defn -main [& args]
  (let [index (parse-args args)]
    (->> *in*
         stream/read-all
         pumlate
         indent
         println
         )))
