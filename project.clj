(defproject dj-marky-markov "0.0.3"
  :description "Markov Chains for Text Generation"
  :url "https://github.com/nnichols/dj-marky-markov"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :bikeshed {:long-lines false}
  :eastwood {:add-linters [:unused-fn-args :unused-private-vars]}
  :main ^:skip-aot dj-marky-markov.core
  :target-path "target/%s"
  :aot :all
  :profiles {:uberjar {:aot :all}})
