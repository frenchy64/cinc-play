; install CinC:
;  git clone https://github.com/Bronsa/CinC
;  cd CinC
;  lein install
(ns cinc-play.core
  (:refer-clojure :exclude [macroexpand-1 macroexpand])
  (:require [cinc.analyzer :refer [analyze macroexpand-1]]
            [cinc.analyzer.passes.jvm.emit-form :refer [emit-form]]
            [clojure.core.match :as m]
            [clojure.pprint :refer [pprint]]))

(def default-macroexpand-1 macroexpand-1)

(defn match-macroexpand-1 [form env]
  (if (and (seq? form)
           (= 'clojure.core.match/match (first form)))
    (let [[_ target & flat-cases] form]
      `(cond
         ~@(apply concat
                  (for [[test then] (partition 2 flat-cases)]
                    [`(= ~test ~target) then]))))
    (default-macroexpand-1 form env)))


(-> '1
    (analyze {:context :statement :locals {}})
    emit-form)

(-> `(m/match 1
              :a 1
              :b 2)
    (analyze {:context :statement :locals {}})
    emit-form
    pprint)

(binding [macroexpand-1 match-macroexpand-1]
  (-> `(m/match 1
                :a 1
                :b 2)
      (analyze {:context :statement :locals {}})
      emit-form
      pprint))
