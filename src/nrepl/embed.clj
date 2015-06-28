(ns ^{:clojure.tools.namespace.repl/load false
      :clojure.tools.namespace.repl/unload false}
  nrepl.embed

  (:require [clojure.tools.nrepl.server :as nrepl]
            [cider.nrepl :refer [cider-middleware]]
            [refactor-nrepl.middleware]

            [clojure.tools.logging :as log]))

(defonce ^:private !repl (atom nil))

(try
  (require 'cemerick.piggieback)

  (catch Exception e
    ;; sorry, no bacon! ;)
    ))

(defn start-nrepl! [{:keys [bind port]
                     :or {bind "127.0.0.1"}}]
  (assert (and port (integer? port)) "Please provide an nREPL port!")

  (when (compare-and-set! !repl nil {:port port})
    (let [server (nrepl/start-server :bind bind
                                     :port port
                                     :handler (->> (conj cider-middleware 'refactor-nrepl.middleware/wrap-refactor 'cemerick.piggieback/wrap-cljs-repl)
                                                   (map resolve)
                                                   (remove nil?)
                                                   (apply nrepl/default-handler)))]
      (swap! !repl assoc :server server)
      (log/info "nREPL server started, port" port)
      server)))

(defn stop-nrepl! []
  (let [{:keys [server] :as repl} @!repl]
    (when (and server
               (compare-and-set! !repl repl nil))
      (nrepl/stop-server server)
      (log/info "nREPL server stopped."))))


(defn ->brepl
  ([]
   (->brepl {:host "127.0.0.1"
             :port 9001}))

  ([{:keys [host port]}]
   (require '[weasel.repl.websocket :as w])
   (eval `(cemerick.piggieback/cljs-repl (weasel.repl.websocket/repl-env :ip ~host :port ~port)))))
