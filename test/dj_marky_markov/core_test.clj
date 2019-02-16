(ns dj-marky-markov.core-test
  (:require [clojure.test :refer :all]
            [dj-marky-markov.core :as dmm]))

(deftest concat-with-space-test
  (testing "Concatenation of strings always includes a space"
    (is (= "Hello World"   (dmm/concat-with-space "Hello" "World")))
    (is (= "Hello, World"  (dmm/concat-with-space "Hello," "World")))
    (is (= "Hello World!"  (dmm/concat-with-space "Hello" "World!")))
    (is (= "Hello, World!" (dmm/concat-with-space "Hello," "World!")))))

(deftest search-text-test
  (testing "A string can be sliced to the last n words"
    (let [test-text "If on a winter's night a traveler"]
      (is (thrown? clojure.lang.ArityException (dmm/search-text test-text 0)))
      (is (= (dmm/search-text test-text 1) "traveler"))
      (is (= (dmm/search-text test-text 2) "a traveler"))
      (is (= (dmm/search-text test-text 3) "night a traveler"))
      (is (= (dmm/search-text test-text 4) "winter's night a traveler"))
      (is (= (dmm/search-text test-text 7) test-text))
      (is (= (dmm/search-text test-text 8) test-text)))))

(deftest string-to-sliding-window-test
  (testing "A string may be sliced into multiple text/transition windows"
    (let [test-text "It was a pleasure to burn."]
      (is (= (dmm/string-to-sliding-window test-text 0) ['("It") '("was") '("a") '("pleasure") '("to") '("burn.")]))
      (is (= (dmm/string-to-sliding-window test-text 1) ['("It" "was") '("was" "a") '("a" "pleasure") '("pleasure" "to") '("to" "burn.")]))
      (is (= (dmm/string-to-sliding-window test-text 5) ['("It" "was" "a" "pleasure" "to" "burn.")]))
      (is (empty? (dmm/string-to-sliding-window test-text 6))))))

(deftest sentence-predicate-test
  (testing "Ensure sentence predicates accurately track English behavior"
    (is (dmm/starts-sentence? ["A" "screaming" "comes" "across" "the" "sky."]))
    (is (not (dmm/starts-sentence? ["124" "was" "spiteful."])))
    (is (not (dmm/starts-sentence? ["—Money" "." "." "." "in" "a" "voice" "that" "rustled."])))
    (is (not (dmm/starts-sentence? ["the" "moment" "one" "learns" "English," "complications" "set" "in."])))
    (is (dmm/sentence-ended? "Dr. Weiss, at forty, knew that her life had been ruined by literature"))
    (is (dmm/sentence-ended? "The story so far: In the beginning the Universe was created."))
    (is (dmm/sentence-ended? "Call me Ishmael."))
    (is (dmm/sentence-ended? "Where now? Who now? When now?"))
    (is (dmm/sentence-ended? "Stop! cried the groaning old man at last, Stop! I did not drag my father beyond this tree"))
    (is (not (dmm/sentence-ended? "The ting go skraaa")))
    (is (not (dmm/sentence-ended? "All this happened, more or less-")))))
