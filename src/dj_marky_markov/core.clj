(ns dj-marky-markov.core
  "A simple Markov Chain for text generation in Clojure"
  (:require [clojure.string :as cs]
            [clojure.set :as cset])
  (:gen-class))

(def first-capital-letter-regex
  "Regex pattern to find words that start sentences"
  (re-pattern "^[A-Z]"))

(def punctuation-regex
  "This is a loose regex detection for string terminators. Needs to be more robust"
  (re-pattern "[\\.\\?\\!]"))

(defn concat-with-space
  "Return `s1` concatenated with `s2` with a space between the two strings"
  [s1 s2]
  (str s1 " " s2))

(defn search-text
  "Take `window-length` words from the end of `sentence`
   to use as the look-up for the next markov transition"
  [stem window-length]
  (let [trailing-part (take-last window-length (cs/split stem #"\s"))]
    (cs/trim (reduce concat-with-space trailing-part))))

(defn string-to-sliding-window
  "Convert `string` into a series of sliding windows that will be sliced into
   a window and a transition word, hence incrementing the partition window."
  [string window-length]
  (partition (inc window-length) 1 (cs/split string #"\s+")))

(defn single-window-to-tuple
  "Split `window` into a look-up and a transition tuple"
  [window]
  (cons (reduce concat-with-space (butlast window)) (list (last window))))

(defn add-entry
  "Update `dictionary` with an `entry` tuple.
   If the look-up already exists, cons the current transition to the list of transitions"
  [dictionary entry]
  (update dictionary (first entry) #(cons (second entry) %)))

(defn build-markov-dictionary
  "Collapse `tuple-set` into a look-up dictionary"
  [tuple-set]
  (reduce add-entry {} tuple-set))

(defn starts-sentence?
  "Boolean predicate that returns true iff the first word of `text-window`
   starts with a capital letter"
  [text-window]
  (boolean (re-find first-capital-letter-regex (first text-window))))

(defn sentence-ended?
  "Boolean predicate that returns true iff `text` contains terminal punctuation"
  [text]
  (boolean (re-find punctuation-regex text)))

(defn markov-sentence
  "Build a sentence starting with a random element in `sentence-starters`, and
   recursively navigate the markov model in `sentence-bodies`, whose transition windows
   are sized by `window-length`"
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
  "Generate `sentences` repeatedly"
  [sentence-starters sentence-bodies window-length sentences]
  (take sentences (repeatedly #(markov-sentence sentence-starters sentence-bodies window-length))))

(defn load-data!
  "Load the file at `path` and split it into tuples of size `window-length`"
  [path window-length]
  (map single-window-to-tuple (string-to-sliding-window (slurp path) window-length)))

(defn write-sentences
  "Generate `copies` sentences from the `tuples` where each transition window is `window-length`"
  [tuples window-length copies]
  (let [sentence-starters (build-markov-dictionary (get (group-by starts-sentence? tuples) true))
        sentence-bodies (build-markov-dictionary tuples)]
    (markov-sentences sentence-starters sentence-bodies window-length copies)))

(defn -main
  "Try me out!"
  [& args]
  (let [window-length (Integer/parseInt (second args))
        tuples (load-data! (first args) window-length)
        results-to-generate (Integer/parseInt (nth args 2))
        generated-text (write-sentences tuples window-length results-to-generate)]
    (doseq [sentence generated-text] (println sentence))))
