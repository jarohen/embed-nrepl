(ns ^{:clojure.tools.namespace.repl/load false
      :clojure.tools.namespace.repl/unload false}
  nrepl.embed

  (:require [clojure.tools.nrepl.server :as nrepl]
            [cider.nrepl :refer [cider-middleware]]
            [refactor-nrepl.middleware]

            [clojure.tools.logging :as log]))

(defonce ^:private !repl (atom nil))

(defn start-nrepl! [{:keys [bind port]
                     :or {bind "127.0.0.1"}}]
  (assert (and port (integer? port)) "Please provide an nREPL port!")

  (when (compare-and-set! !repl nil {:port port})
    (let [server (nrepl/start-server :bind bind
                                     :port port
                                     :handler (->> (conj cider-middleware 'refactor-nrepl.middleware/wrap-refactor)
                                                   (map resolve)
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
