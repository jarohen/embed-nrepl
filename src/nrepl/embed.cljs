(ns nrepl.embed
  (:require [weasel.repl :as w]))

(defn connect-brepl!
  ([]
   (connect-brepl! {:host "localhost"
                    :port 9001}))

  ([{:keys [host port]}]
   (when-not (w/alive?)
     (w/connect (str "ws://" host ":" port)))))
