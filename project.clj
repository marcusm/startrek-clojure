(defproject startrek "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/data.generators  "0.1.2"]
                 [org.clojure/math.numeric-tower  "0.0.4"]
                 [jline  "0.9.94"]
                 [cider/cider-nrepl "0.7.0-SNAPSHOT"]]
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
             :dev {:dependencies [[midje "1.6.3"]]}
             }
  :main startrek.core
  )
