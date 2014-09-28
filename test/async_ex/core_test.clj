(ns async-ex.core-test
  (:require [clojure.test :refer :all]
            [async-ex.core :refer :all]
            [clojure.core.async :as async :refer (>! >!! go thread <! <!! chan timeout)]))

(deftest ex-chan-test
  (testing "ex-chan"
    (let [erno (atom nil) 
          ex-ch (ex-chan (fn [e] (reset! erno e)))]
      (>!! ex-ch true)
      (<!! (timeout 1000))
      (is (= true @erno)))))

(deftest go-ex-test
  (testing "go-ex"
    (let [erno (atom "b")
          ex (Exception. "Test Exception") 
          ex-ch (ex-chan (fn [e] (reset! erno e))) 
          ]
      (go-ex ex-ch
             (throw ex))
      (<!! (timeout 1000)) ; Give time for change to propagate
      (is (= ex @erno))

      (reset! erno true)
      
      (go-ex ex-ch
             (identity "a"))
      (<!! (timeout 1000))
      (is @erno))))

(deftest thread-ex-test
  (testing "thread-ex"
    (let [erno (atom "b")
          ex (Exception. "Test Exception") 
          ex-ch (ex-chan (fn [e] (reset! erno e))) 
          ]
      (thread-ex ex-ch
             (throw ex))
      (<!! (timeout 1000)) ; Give time for change to propagate
      (is (= ex @erno))

      (reset! erno true)
      
      (thread-ex ex-ch
             (identity "a"))
      (<!! (timeout 1000))
      (is @erno))))
