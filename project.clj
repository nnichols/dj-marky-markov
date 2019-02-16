(defproject dj-marky-markov "0.0.1"
  :description "Markov Chains for Text Generation"
  :url "https://github.com/nnichols/dj-marky-markov"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :bikeshed {:long-lines false}
  :eastwood {:add-linters [:unused-fn-args :unused-private-vars]}
  :main ^:skip-aot dj-marky-markov.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
