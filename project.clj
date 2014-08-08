(defproject startrek "0.1.0-SNAPSHOT"
  :description "Re-write of old StarTrek conole app in clojure."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojure-lanterna "0.9.4"]]
  :resource-paths ["src/startrek/pages"]
  :repl-options {:nrepl-middleware
                 [cider.nrepl.middleware.classpath/wrap-classpath
                  cider.nrepl.middleware.complete/wrap-complete
                  cider.nrepl.middleware.info/wrap-info
                  cider.nrepl.middleware.inspect/wrap-inspect
                  cider.nrepl.middleware.macroexpand/wrap-macroexpand
                  cider.nrepl.middleware.stacktrace/wrap-stacktrace
                  cider.nrepl.middleware.trace/wrap-trace]}
  :profiles {:1.5 {:dependencies [[org.clojure/clojure "1.5.0"]]}
             :repl {:plugins [[cider/cider-nrepl "0.7.0"]]}
             }
  :main startrek.core
  :uberjar-name "startrek-standalone.jar"
  )
