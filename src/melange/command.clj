(ns melange.command)

(defmulti handle-command (fn [_ [command _]] command))
