(ns async-ex.core
  (require [clojure.core.async :as async :refer (>! >!! go thread <! <!! chan)]))

(defn ex-chan 
  ([handler]
     (ex-chan handler (chan)))
  ([handler c]
   (async/go 
     (while true
       (handler (<! c))))
   c))

(defn coerce-seq 
  "Takes either a seq or single element and returns a seq"
  [x]
  (flatten [x]))

(defmacro go-ex [ex-chs & body]
  `(go
     (try 
       ~@body
       (catch Exception ~'ex
         ~@(map (fn [c] (list '>!! c 'ex)) (coerce-seq ex-chs))))))

(defmacro thread-ex [ex-chs & body]
  `(thread
     (try 
       ~@body
       (catch Exception ~'ex
         ~@(map (fn [c] (list '>!! c 'ex)) (coerce-seq ex-chs))))))

#_ (def erno (atom ""))
#_ (def erno1 (atom ""))
#_ (def ex-ch (ex-chan (fn [e] (reset! erno e))))
#_ (def ex-ch1 (ex-chan (fn [e] (reset! erno1 e))))

#_ (macroexpand '(go-ex 
     [ex-ch ex-ch1]
     (throw (Exception.))))
