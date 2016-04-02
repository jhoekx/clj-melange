(ns melange.tag-test
  (:require [clojure.test :refer :all]
            [melange.core :refer :all]))

(defn- uuid? [val]
  (= 36 (count (.toString val))))

(deftest tag-commands
  (let [tag-added (handle-command default-state [:add-tag {:name "tag"}])]
    (testing "Add a tag"
      (is (= 3 (count tag-added)))
      (is (= :tag-added (first tag-added)))
      (is (uuid? (second tag-added)))
      (is (= "tag" (last tag-added)))))
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
                                                                        :key "text"}])]
    (testing "Add a variable"
      (is (= "blah" (-> (find-tag-by-name "tag" text-var) :vars (get "text")))))
    (testing "Add a second variable"
      (is (= 2 (-> (find-tag-by-name "tag" list-var) :vars count)))
      (is (= [1 2 3] (-> (find-tag-by-name "tag" list-var) :vars (get "list")))))
    (testing "Remove a variable"
      (is (= 1 (-> (find-tag-by-name "tag" removed-var) :vars count)))
      (is (= [1 2 3] (-> (find-tag-by-name "tag" removed-var) :vars (get "list")))))))
