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

(defn start-nrepl! [{:keys [bind port cljs-opts weasel-opts]
                     :or {bind "127.0.0.1"}}]
  (assert (and port (integer? port)) "Please provide an nREPL port!")

  (when (compare-and-set! !repl nil {:port port})
    (try
      (let [server (nrepl/start-server :bind bind
                                       :port port
                                       :handler (->> (conj cider-middleware 'refactor-nrepl.middleware/wrap-refactor 'cemerick.piggieback/wrap-cljs-repl)
                                                     (map resolve)
                                                     (remove nil?)
                                                     (apply nrepl/default-handler)))]

        (try
          (spit ".nrepl-port" port)
          (catch Exception _))

        (swap! !repl #(assoc %
                        :server server
                        :cljs-opts cljs-opts
                        :weasel-opts weasel-opts))

        (log/info "nREPL server started, port" port)
        server)

      (catch Exception e
        (reset! !repl nil)))))

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
   (eval `(apply cemerick.piggieback/cljs-repl (apply weasel.repl.websocket/repl-env (apply concat (seq ~(merge (:weasel-opts @!repl)
                                                                                                                {:ip host
                                                                                                                 :port port}))))
                 (apply concat (seq ~(:cljs-opts @!repl)))))))
