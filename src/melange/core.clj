(ns melange.core)

(def default-state {:tags  []
                    :items []})

(defmulti handle-event (fn [_ [event _]] event))

(defn- make-thing [name]
  {:name name
   :vars {}})

(defn- add-thing [state [thing-type name]]
  (->> (make-thing name)
       (conj (thing-type state))
       (assoc state thing-type)))

(defn- remove-thing [state [thing-type name]]
  (->> (thing-type state)
       (filter #(not= name (:name %)))
       (assoc state thing-type)))

(defn- change-thing [name f]
  (fn [thing]
    (if (= name (:name thing))
      (f thing)
      thing)))

(defn- add-variable-to-thing [state [thing-type name key value]]
  (->> (thing-type state)
       (map (change-thing name #(assoc-in % [:vars key] value)))
       (assoc state thing-type)))

(defn- remove-variable-from-thing [state [thing-type name key]]
  (->> (thing-type state)
       (map (change-thing name #(assoc % :vars (dissoc (:vars %) key))))
       (assoc state thing-type)))

(defmethod handle-event :tag-added
  [state [_ {:keys [name]}]]
  (add-thing state [:tags name]))

(defmethod handle-event :tag-removed
  [state [_ {:keys [name]}]]
  (remove-thing state [:tags name]))

(defmethod handle-event :variable-added-to-tag
  [state [_ {:keys [name key value]}]]
  (add-variable-to-thing state [:tags name key value]))

(defmethod handle-event :variable-removed-from-tag
  [state [_ {:keys [name key]}]]
  (remove-variable-from-thing state [:tags name key]))

(defmethod handle-event :item-added
  [state [_ {:keys [name]}]]
  (add-thing state [:items name]))

(defmethod handle-event :item-removed
  [state [_ {:keys [name]}]]
  (remove-thing state [:items name]))

(defmethod handle-event :variable-added-to-item
  [state [_ {:keys [name key value]}]]
  (add-variable-to-thing state [:items name key value]))

(defmethod handle-event :variable-removed-from-item
  [state [_ {:keys [name key]}]]
  (remove-variable-from-thing state [:items name key]))
