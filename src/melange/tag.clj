(ns melange.tag
  (:require [melange.command :refer [handle-command]]
            [melange.event :refer [handle-event]]
            [melange.thing :refer :all]))

(defmethod handle-event :tag-added
  [state [_ {:keys [id name]}]]
  (add-thing state :tags id name))

(defmethod handle-event :tag-removed
  [state [_ {:keys [id]}]]
  (remove-thing state :tags id))

(defmethod handle-event :variable-added-to-tag
  [state [_ {:keys [id key value]}]]
  (add-variable-to-thing state :tags id key value))

(defmethod handle-event :variable-removed-from-tag
  [state [_ {:keys [id key]}]]
  (remove-variable-from-thing state :tags id key))

(defmethod handle-event :child-added-to-tag
  [state [_ {:keys [id child-id]}]]
  (add-child-to-thing state :tags id child-id))

(defmethod handle-event :child-removed-from-tag
  [state [_ {:keys [id child-id]}]]
  (remove-child-from-thing state :tags id child-id))

(defmethod handle-command :add-tag
  [state [_ {:keys [name]}]]
  (if (name-exists? name (:tags state))
    (throw (RuntimeException. "Tag already exists"))
    [:tag-added {:id   (generate-id)
                 :name name}]))
