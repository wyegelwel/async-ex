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
    (let [erno1 (atom nil)
          erno2 (atom nil)
          ex (Exception. "Test Exception") 
          ex-ch1 (ex-chan (fn [e] (reset! erno1 e)))
          ex-ch2 (ex-chan (fn [e] (reset! erno2 e)))
          ]
      (go-ex ex-ch1
             (throw ex))
      (<!! (timeout 1000)) ; Give time for change to propagate
      (is (= ex @erno1))

      (reset! erno1 true)
      
      (go-ex ex-ch1
             (identity "a"))
      (<!! (timeout 1000))
      (is @erno1)
    
      (go-ex 
        [ex-ch1 ex-ch2]
        (throw ex))
      (<!! (timeout 1000))
      (is (= @erno1 @erno2 ex)))))

(deftest thread-ex-test
  (testing "thread-ex"
    (let [erno1 (atom nil)
          erno2 (atom nil)
          ex (Exception. "Test Exception") 
          ex-ch1 (ex-chan (fn [e] (reset! erno1 e)))
          ex-ch2 (ex-chan (fn [e] (reset! erno2 e)))
          ]
      (thread-ex ex-ch1
             (throw ex))
      (<!! (timeout 1000)) ; Give time for change to propagate
      (is (= ex @erno1))

      (reset! erno1 true)

      (thread-ex ex-ch1
             (identity "a"))
      (<!! (timeout 1000))
      (is @erno1)

      (thread-ex 
        [ex-ch1 ex-ch2]
        (throw ex))
      (<!! (timeout 1000))
      (is (= @erno1 @erno2 ex)))))
