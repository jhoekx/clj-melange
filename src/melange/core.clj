(ns melange.core
  (:require [melange.event :as event]
            [melange.item]
            [melange.tag]))

(def default-state {:tags  []
                    :items []})

(defn handle-event [state event]
  (event/handle-event state event))
