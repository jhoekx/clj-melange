(ns melange.thing
  (:import (java.util UUID)))

(defn generate-id []
  (str (UUID/randomUUID)))

(defn name-exists? [name coll]
  (->> coll
       (filter #(= name (:name %)))
       count
       (not= 0)))

(defn- make-thing [id name]
  {:id   id
   :name name
   :vars {}})

(defn add-thing [state [thing-type id name]]
  (->> (make-thing id name)
       (conj (thing-type state))
       (assoc state thing-type)))

(defn remove-thing [state [thing-type id]]
  (->> (thing-type state)
       (filter #(not= id (:id %)))
       (assoc state thing-type)))

(defn- change-thing [id f]
  (fn [thing]
    (if (= id (:id thing))
      (f thing)
      thing)))

(defn add-variable-to-thing [state [thing-type id key value]]
  (->> (thing-type state)
       (map (change-thing id #(assoc-in % [:vars key] value)))
       (assoc state thing-type)))

(defn remove-variable-from-thing [state [thing-type id key]]
  (->> (thing-type state)
       (map (change-thing id #(assoc % :vars (dissoc (:vars %) key))))
       (assoc state thing-type)))
