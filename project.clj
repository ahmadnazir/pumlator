(defproject pumlator "0.1.0-SNAPSHOT"
  :description "Generate plant uml from a dsl"
  :url "http://github.com/ahmandazir/pumlator"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [instaparse "1.4.10"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 ]
  :repl-options {:init-ns app.core}
  :main ahmadnazir.pumlator.core
  )
