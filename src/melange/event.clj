(ns melange.event)

(defmulti handle-event (fn [_ [event _]] event))
