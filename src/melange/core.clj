(ns melange.core)

(def default-state {:tags  []
                    :items []})

(defmulti handle-event (fn [_ [event _]] event))

(defn- make-thing [id name]
  {:id   id
   :name name
   :vars {}})

(defn- add-thing [state [thing-type id name]]
  (->> (make-thing id name)
       (conj (thing-type state))
       (assoc state thing-type)))

(defn- remove-thing [state [thing-type id]]
  (->> (thing-type state)
       (filter #(not= id (:id %)))
       (assoc state thing-type)))

(defn- change-thing [id f]
  (fn [thing]
    (if (= id (:id thing))
      (f thing)
      thing)))

(defn- add-variable-to-thing [state [thing-type id key value]]
  (->> (thing-type state)
       (map (change-thing id #(assoc-in % [:vars key] value)))
       (assoc state thing-type)))

(defn- remove-variable-from-thing [state [thing-type id key]]
  (->> (thing-type state)
       (map (change-thing id #(assoc % :vars (dissoc (:vars %) key))))
       (assoc state thing-type)))

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

(defmethod handle-event :item-added
  [state [_ {:keys [id name]}]]
  (add-thing state [:items id name]))

(defmethod handle-event :item-removed
  [state [_ {:keys [id]}]]
  (remove-thing state [:items id]))

(defmethod handle-event :variable-added-to-item
  [state [_ {:keys [id key value]}]]
  (add-variable-to-thing state [:items id key value]))

(defmethod handle-event :variable-removed-from-item
  [state [_ {:keys [id key]}]]
  (remove-variable-from-thing state [:items id key]))
