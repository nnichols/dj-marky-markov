(ns dj-marky-markov.core
  (require [clojure.string :as cs])
  (:gen-class))

(defn string-to-sliding-window
  [string window-length]
  (partition window-length 1 (cs/split string #"\s+")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [path (first args)
        window-length (Integer/parseInt (second args))]
    (println (string-to-sliding-window (slurp path) window-length))))
