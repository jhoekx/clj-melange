(defproject melange "0.1.0-SNAPSHOT"
  :description "A lightweigt CMDB"
  :url "http://github.com/jhoekx/melange"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :plugins      [[lein-auto "0.1.2"]]}})
