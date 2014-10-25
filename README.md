# async-ex

After much thought, I've decided this is a terrible way to handle exceptions with async. I've written a post about better ways to [handle exceptions in async](http://wyegelwel.github.io/Error-Handling-with-Clojure-Async/). However, I'm leaving this code up for the sake of cataloging bad ideas. 

### Previous README

Error handling with clojure async can be a little hairy. Prior to Clojure 1.6.0, exceptions thrown in `go` or `thread` blocks that weren't caught in the block were simply dropped. Since Clojure 1.6.0, exceptions not caught in the block are printed to stderr. To resolve this, you either need to wrap every async block in try/catch or set a [DefaultUncaughtExceptionHandler](http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.UncaughtExceptionHandler.html) or use the [method](http://martintrojer.github.io/clojure/2014/03/09/working-with-coreasync-exceptions-in-go-blocks/) suggested by David Nolen and explained by Martin Trojer. I've found that all of these solutions leave something to be desired and that is why I created this library.


The library is simple, yet powerful. At it's core is a concept called an exception chan (`ex-chan`). You provide it an exception handler and optionally a channel and it returns a channel. The exception channel is constantly passing anything put onto it to your exception handler. Then to put this exception chan to work, async-ex provides it's own version of both `go` and `thread`, `go-ex` and `thread-ex`. You pass either a single ex-chan or a collection of ex-chans and then the body you want executed. If no exceptions occur, `go-ex` and `thread-ex` perform exactly the same as `go` and `thread`. However, in the event of an exception, the exception is put onto all the ex-chans you provided. 

Here is a simple example:

```Clojure

(def print-email-close-ex-ch 
             (ex-chan (fn [ex] (println ex) (email-devs ex) (System/exit 1))))

(go-ex 
   print-email-close-ex-ch
   (... awesome code ...))

```

The reason to use async-ex is to separate your normal logic from your exception handling logic. Additionally, you can modularize your exception handling logic by having different ex-chans. 

## Usage

Leiningen dependency: 

`[async-ex "0.1.0-SNAPSHOT"]`

Hello world usage:

```Clojure 
(def print-ex-ch (ex-chan (fn [ex] (println ex))))

(go-ex 
   print-ex-ch
   (throw (Exception. "Hello World!")))
```

Modularize your exception logic by having different exception channels. Then supply go-ex blocks with an assortments of those exception channels.

```Clojure

(def print-ex-chan (ex-chan (fn [ex] (println ex))))

(def email-ex-chan (ex-chan (fn [ex] (email-devs ex))))

(def log-ex-chan (ex-chan (fn [ex] (log ex))))

(go-ex 
   [print-ex-chan log-ex-chan]
   (parse-user-input))

(go-ex 
  [print-ex-chan email-ex-chan log-ex-chan]
  (post-to-server)
```
## License

Distributed under the Eclipse Public License, the same as Clojure.
