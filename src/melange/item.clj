(ns melange.item
  (:require [melange.event :refer [handle-event]]
            [melange.thing :refer :all]))

(defmethod handle-event :item-added
  [state [_ {:keys [id name]}]]
  (add-thing state :items id name))

(defmethod handle-event :item-removed
  [state [_ {:keys [id]}]]
  (-> (remove-thing state :items id)
      (remove-child-from-all :tags id)
      (remove-child-from-all :items id)))

(defmethod handle-event :variable-added-to-item
  [state [_ {:keys [id key value]}]]
  (add-variable-to-thing state :items id key value))

(defmethod handle-event :variable-removed-from-item
  [state [_ {:keys [id key]}]]
  (remove-variable-from-thing state :items id key))

(defmethod handle-event :child-added-to-item
  [state [_ {:keys [id child-id]}]]
  (add-child-to-thing state :items id child-id))

(defmethod handle-event :child-removed-from-item
  [state [_ {:keys [id child-id]}]]
  (remove-child-from-thing state :items id child-id))
