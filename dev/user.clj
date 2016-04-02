(ns user
  (:require [clojure.test]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.repl]))

(defn rt []
  (case (refresh)
    :ok (clojure.test/run-all-tests #"melange.*-test")
    (clojure.repl/pst)))
