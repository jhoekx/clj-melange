(ns melange.core-test
  (:require [clojure.test :refer :all]
            [melange.core :refer :all]))

(deftest tags
  (let [one-tag (handle-event default-state [:tag-added {:id "1" :name "tag1"}])
        second-tag (handle-event one-tag [:tag-added {:id "2" :name "tag2"}])
        removed-tag (handle-event second-tag [:tag-removed {:id "1"}])]
    (testing "Add one tag"
      (is (= "tag1" (-> one-tag :tags first :name))))
    (testing "Add a second tag"
      (is (= 2 (-> second-tag :tags count)))
      (is (= "tag2" (-> second-tag :tags second :name))))
    (testing "Remove a tag"
      (is (= 1 (-> removed-tag :tags count)))
      (is (= "tag2" (-> removed-tag :tags first :name))))))

(defn- find-tag-by-name [name state]
  (->> (:tags state)
       (filter #(= name (:name %)))
       first))

(deftest tag-variables
  (let [tag (handle-event default-state [:tag-added {:id "1" :name "tag"}])
        text-var (handle-event tag [:variable-added-to-tag {:id    "1"
                                                            :key   "text"
                                                            :value "blah"}])
        list-var (handle-event text-var [:variable-added-to-tag {:id    "1"
                                                                 :key   "list"
                                                                 :value [1 2 3]}])
        removed-var (handle-event list-var [:variable-removed-from-tag {:id  "1"
                                                                        :key "text"}])]
    (testing "Add a variable"
      (is (= "blah" (-> (find-tag-by-name "tag" text-var) :vars (get "text")))))
    (testing "Add a second variable"
      (is (= 2 (-> (find-tag-by-name "tag" list-var) :vars count)))
      (is (= [1 2 3] (-> (find-tag-by-name "tag" list-var) :vars (get "list")))))
    (testing "Remove a variable"
      (is (= 1 (-> (find-tag-by-name "tag" removed-var) :vars count)))
      (is (= [1 2 3] (-> (find-tag-by-name "tag" removed-var) :vars (get "list")))))))

(deftest items
  (let [one-item (handle-event default-state [:item-added {:id "1" :name "item1"}])
        two-items (handle-event one-item [:item-added {:id "2" :name "item2"}])
        removed-item (handle-event two-items [:item-removed {:id "1"}])]
    (testing "Add an item"
      (is (= "item1" (-> one-item :items first :name))))
    (testing "Adding a second item"
      (is (= 2 (-> two-items :items count)))
      (is (= "item2" (-> two-items :items second :name))))
    (testing "Removing an item"
      (is (= 1 (-> removed-item :items count))))))

(defn- find-item-by-name [name state]
  (->> (:items state)
       (filter #(= name (:name %)))
       first))

(deftest item-variables
  (let [item (handle-event default-state [:item-added {:id "1" :name "item"}])
        text-var (handle-event item [:variable-added-to-item {:id    "1"
                                                              :key   "text"
                                                              :value "blah"}])
        list-var (handle-event text-var [:variable-added-to-item {:id    "1"
                                                                  :key   "list"
                                                                  :value [1 2 3]}])
        removed-var (handle-event list-var [:variable-removed-from-item {:id  "1"
                                                                         :key "text"}])]
    (testing "Add a variable"
      (is (= "blah" (-> (find-item-by-name "item" text-var) :vars (get "text")))))
    (testing "Add a second variable"
      (is (= 2 (-> (find-item-by-name "item" list-var) :vars count)))
      (is (= [1 2 3] (-> (find-item-by-name "item" list-var) :vars (get "list")))))
    (testing "Remove a variable"
      (is (= 1 (-> (find-item-by-name "item" removed-var) :vars count)))
      (is (= [1 2 3] (-> (find-item-by-name "item" removed-var) :vars (get "list")))))))
