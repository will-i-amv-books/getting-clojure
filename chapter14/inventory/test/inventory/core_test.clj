(ns inventory.core-test
    (:require [clojure.test :refer :all])
    (:require [inventory.core :as i])
    (:require [clojure.test.check.properties :as prop])
    (:require [clojure.test.check.generators :as gen])
    (:require [clojure.test.check :as tc]))

;; Traditional testing

(def books
    [{:title "2001" :author "Clarke" :copies 21}
     {:title "Emma" :author "Austen" :copies 10}
     {:title "Misery" :author "King" :copies 101}])

(deftest test-finding-books
    (is (not (nil? (i/find-by-title "Emma" books)))))

(deftest test-finding-books-better
    (is (not (nil? (i/find-by-title "Emma" books))))
    (is (nil? (i/find-by-title "XYZZY" books))))

(deftest test-basic-inventory
    (testing "Finding books"
        (is (not (nil? (i/find-by-title "Emma" books))))
        (is (nil? (i/find-by-title "XYZZY" books))))
    (testing "Copies in inventory"
        (is (= 10 (i/number-of-copies-of "Emma" books)))))

;; Property-based testing

(def title-gen (gen/such-that not-empty gen/string-alphanumeric))

(def author-gen (gen/such-that not-empty gen/string-alphanumeric))

(def copies-gen (gen/such-that (complement zero?) gen/pos-int))

(def book-gen
    (gen/hash-map :title title-gen :author author-gen :copies copies-gen))

(def inventory-gen 
    (gen/not-empty (gen/vector book-gen)))

(def inventory-and-book-gen
    (gen/let [inventory inventory-gen
              book (gen/elements inventory)]
    {:inventory inventory :book book}))

(defn my-property-test []
    (tc/quick-check 50
        (prop/for-all [i-and-b inventory-and-book-gen]
            (=  (i/find-by-title (-> i-and-b :book :title) (:inventory i-and-b)) (:book i-and-b)))))
