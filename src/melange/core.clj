(ns melange.core
  (:require [melange.event :as event]
            [melange.command :as command]
            [melange.item]
            [melange.tag]))

(def default-state {:tags  []
                    :items []})

(defn handle-command [state command]
  (command/handle-command state command))

(defn handle-event [state event]
  (event/handle-event state event))

(defn apply-events [state events]
  (reduce handle-event state events))
