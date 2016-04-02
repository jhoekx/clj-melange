(ns melange.item-test
  (:require [clojure.test :refer :all]
            [melange.core :refer :all]))

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

(deftest item-children
  (let [items (apply-events default-state [[:item-added {:id "1" :name "item1"}]
                                           [:item-added {:id "2" :name "item2"}]
                                           [:item-added {:id "3" :name "item3"}]])
        first-child (handle-event items [:child-added-to-item {:id "1" :child-id "2"}])
        second-child (handle-event first-child [:child-added-to-item {:id "1" :child-id "3"}])]
    (testing "Add a child"
      (is (= ["2"] (-> (find-item-by-name "item1" first-child) :children))))
    (testing "Adding a second child"
      (is (= 2 (-> (find-item-by-name "item1" second-child) :children count)))
      (is (= ["2" "3"] (-> (find-item-by-name "item1" second-child) :children))))
    (testing "Removing a child"
      (let [removed-child (handle-event second-child [:child-removed-from-item {:id       "1"
                                                                                :child-id "3"}])]
        (is (= 1 (-> (find-item-by-name "item1" removed-child) :children count)))
        (is (= ["2"] (-> (find-item-by-name "item1" removed-child) :children)))))
    (testing "Removing an item removes it from other items"
      (let [removed-child (handle-event second-child [:item-removed {:id "3"}])]
        (is (= 1 (-> (find-item-by-name "item1" removed-child) :children count)))
        (is (= ["2"] (-> (find-item-by-name "item1" removed-child) :children)))))))
