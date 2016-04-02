(ns melange.tag
  (:require [melange.event :refer [handle-event]]
            [melange.thing :refer :all]))

(defmethod handle-event :tag-added
  [state [_ {:keys [id name]}]]
  (add-thing state [:tags id name]))

(defmethod handle-event :tag-removed
  [state [_ {:keys [id]}]]
  (remove-thing state [:tags id]))

(defmethod handle-event :variable-added-to-tag
  [state [_ {:keys [id key value]}]]
  (add-variable-to-thing state [:tags id key value]))

(defmethod handle-event :variable-removed-from-tag
  [state [_ {:keys [id key]}]]
  (remove-variable-from-thing state [:tags id key]))