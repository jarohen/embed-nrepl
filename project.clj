(defproject jarohen/embed-nrepl "0.1.1-SNAPSHOT"
  :description "A micro-library to start up an nREPL server with my opinionated defaults"
  :url "https://github.com/james-henderson/embed-nrepl"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0-RC2"]

                 [org.clojure/tools.nrepl "0.2.10"]
                 [cider/cider-nrepl "0.9.1"]
                 [refactor-nrepl "1.0.5"]

                 [org.clojure/tools.logging "0.3.1"]])
