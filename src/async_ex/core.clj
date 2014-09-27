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


(defmacro go-ex [ex-ch & body]
  `(go
     (try 
       ~@body
       (catch Exception ex#
         (>! ~ex-ch ex#)))))

(defmacro thread-ex [ex-ch & body]
  `(thread
     (try 
       ~@body
       (catch Exception ex#
         (>!! ~ex-ch ex#)))))
