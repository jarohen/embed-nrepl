(defproject jarohen/embed-nrepl "0.1.0"
  :description "A micro-library to start up an nREPL server with my opinionated defaults"
  :url "https://github.com/james-henderson/embed-nrepl"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]

                 [org.clojure/tools.nrepl "0.2.10"]
                 [cider/cider-nrepl "0.8.2"]
                 [refactor-nrepl "1.0.5"]

                 [org.clojure/tools.logging "0.3.1"]])
