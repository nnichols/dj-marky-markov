(ns dj-marky-markov.core
  (:require [clojure.string :as cs])
  (:gen-class))

(def first-capital-letter-regex
  (re-pattern "^[A-Z]"))

;;HACK - This is fuzzy detection for string terminators. Needs to be more robust
(def punctuation-regex
  (re-pattern "[\\.\\?\\!]"))

(defn concat-with-space
  [s1 s2]
  (str s1 " " s2))

(defn search-text
  [stem window-length]
  (let [trailing-part (take-last window-length (cs/split stem #"\s"))]
    (cs/trim (reduce concat-with-space trailing-part))))

(defn string-to-sliding-window
  [string window-length]
  (partition (inc window-length) 1 (cs/split string #"\s+")))

(defn single-window-to-tuple
  [window]
  (cons (reduce concat-with-space (butlast window)) (list (last window))))

(defn add-entry
  [dictionary entry]
  (update dictionary (first entry) #(cons (second entry) %)))

(defn build-markov-dictionary
  [tuple-set]
  (reduce add-entry {} tuple-set))

(defn starts-sentence?
  [text-window]
  (boolean (re-find first-capital-letter-regex (first text-window))))

(defn sentence-ended?
  [text]
  (boolean (re-find punctuation-regex text)))

(defn markov-sentence
  [sentence-starters sentence-bodies window-length]
  (loop [sentence (rand-nth (keys sentence-starters))]
    (if (sentence-ended? sentence)
      ;; We want to trim off anything that trails after punctuation
      ;; e.g. "I went to the store. Hello" -> "I went to the store."
      (str (first (cs/split sentence punctuation-regex))
           (re-find punctuation-regex sentence))
      (let [look-up (search-text sentence window-length)
            added-text (rand-nth (get sentence-bodies look-up ["."]))]
        (recur (concat-with-space sentence added-text))))))

(defn markov-sentences
  [sentence-starters sentence-bodies window-length sentences]
  (take sentences (repeatedly #(markov-sentence sentence-starters sentence-bodies window-length))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [path (first args)
        window-length (Integer/parseInt (second args))
        text-tuples (map single-window-to-tuple (string-to-sliding-window (slurp path) window-length))
        split-text-tuples (group-by starts-sentence? text-tuples)
        sentence-starters (build-markov-dictionary (get split-text-tuples true))
        sentence-bodies (build-markov-dictionary text-tuples)]
    (doseq [line (markov-sentences sentence-starters sentence-bodies window-length 10)]
      (println line))))
