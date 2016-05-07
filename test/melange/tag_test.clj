(ns melange.tag-test
  (:require [clojure.test :refer :all]
            [melange.core :refer :all]))

(defn- uuid? [val]
  (= 36 (count (.toString val))))

(deftest tag-commands
  (let [tag-added (handle-command default-state [:add-tag {:name "tag"}])]
    (testing "Add a tag"
      (is (= 2 (count tag-added)))
      (is (= :tag-added (first tag-added)))
      (is (uuid? (:id (last tag-added))))
      (is (= "tag" (:name (last tag-added))))))
  (let [state-with-tag (handle-event default-state [:tag-added {:id "1" :name "tag"}])]
    (testing "Do not allow duplicate tag names"
      (is (thrown? RuntimeException (handle-command state-with-tag [:add-tag {:name "tag"}]))))))

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
                                                                        :key "text"}])
        get-variable (fn [state key]
                       (-> (find-tag-by-name "tag" state) :vars (get key)))
        count-variables (fn [state]
                          (-> (find-tag-by-name "tag" state) :vars count))]
    (testing "Add a variable"
      (is (= "blah" (get-variable text-var "text"))))
    (testing "Add a second variable"
      (is (= 2 (count-variables list-var)))
      (is (= [1 2 3] (get-variable list-var "list"))))
    (testing "Remove a variable"
      (is (= 1 (count-variables removed-var)))
      (is (= [1 2 3] (get-variable removed-var "list"))))))

(deftest tag-children
  (let [tag-and-item (apply-events default-state [[:tag-added {:id "1" :name "tag"}]
                                                  [:item-added {:id "2" :name "item1"}]
                                                  [:item-added {:id "3" :name "item2"}]])
        get-children (fn [state]
                       (-> (find-tag-by-name "tag" state) :children))
        count-children (fn [state]
                         (-> (find-tag-by-name "tag" state) :children count))
        child (handle-event tag-and-item [:child-added-to-tag {:id "1" :child-id "2"}])
        second-child (handle-event child [:child-added-to-tag {:id "1" :child-id "3"}])]
    (testing "Add a child"
      (is (= ["2"] (get-children child))))
    (testing "Add a second child"
      (is (= 2 (count-children second-child)))
      (is (= ["2" "3"] (get-children second-child))))
    (testing "Remove a child from a tag"
      (let [removed-child (handle-event second-child [:child-removed-from-tag {:id       "1"
                                                                               :child-id "2"}])]
        (is (= 1 (count-children removed-child)))
        (is (= ["3"] (get-children removed-child)))))
    (testing "Removing a child removes it from all tags"
      (let [removed-child (handle-event second-child [:item-removed {:id "2"}])]
        (is (= 1 (count-children removed-child)))
        (is (= ["3"] (get-children removed-child)))))))
