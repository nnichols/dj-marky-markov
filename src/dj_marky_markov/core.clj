(ns dj-marky-markov.core
  (require [clojure.string :as cs])
  (:gen-class))

(def first-capital-letter-regex
  (re-pattern "^[A-Z]"))

;;HACK - This is fuzzy detection for string terminators. Needs to be more robust
(def punctuation-regex
  (re-pattern "[\\.\\?\\!]"))

(defn string-to-sliding-window
  [string window-length]
  (partition window-length 1 (cs/split string #"\s+")))

;; A binary str that adds a center spce is common, break it our
(defn single-window-to-tuple
  [window]
  (cons (reduce #(str %1 " " %2) (butlast window)) (list (last window))))

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

(defn markov-sentence-builder
  [root search-phrase dictionary]
  (loop [sentence root
         look-up  search-phrase]
    (if (sentence-ended? sentence)
      ;; We want to trim off anything that trails after punctuation
      ;; e.g. "I went to the store. Hello" -> "I went to the store."
      (str (first (cs/split sentence punctuation-regex))
           (re-find punctuation-regex sentence))
      (let [added-text (rand-nth (get dictionary look-up ["."]))]
        (recur (str sentence " " added-text)
               ;;HACK - Needs to be expanded to take (window-length - 1) words from starting text
               ;;       and append that to the added-text
               (str (last (cs/split sentence #"\s")) " " added-text))))))

;;ADD - Let this generate n sentences
(defn markov-sentence
  [sentence-starters sentence-bodies]
  (let [starting-text (rand-nth (keys sentence-starters))
        added-text (rand-nth (get sentence-starters starting-text))]
    (markov-sentence-builder (str starting-text " " added-text)
                             ;;HACK - Needs to be expanded to take (window-length - 1) words from starting text
                             ;;       and append that to the added-text
                             (str (last (cs/split starting-text #"\s")) " " added-text)
                             sentence-bodies)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [path (first args)
        window-length (Integer/parseInt (second args))
        text-tuples (map single-window-to-tuple (string-to-sliding-window (slurp path) window-length))
        split-text-tuples (group-by starts-sentence? text-tuples)
        sentence-starters (build-markov-dictionary (get split-text-tuples true))
        sentence-bodies (build-markov-dictionary text-tuples)
        _ (println (markov-sentence sentence-starters sentence-bodies))
        _ (println (markov-sentence sentence-starters sentence-bodies))
        _ (println (markov-sentence sentence-starters sentence-bodies))
        _ (println (markov-sentence sentence-starters sentence-bodies))
        _ (println (markov-sentence sentence-starters sentence-bodies))]))
