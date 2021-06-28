(ns dj-marky-markov.core-test
  (:require [clojure.test :as t]
            [dj-marky-markov.core :as dmm]))

(t/deftest concat-with-space-test
  (t/testing "Concatenation of strings always includes a space"
    (t/is (= "Hello World"   (dmm/concat-with-space "Hello" "World")))
    (t/is (= "Hello, World"  (dmm/concat-with-space "Hello," "World")))
    (t/is (= "Hello World!"  (dmm/concat-with-space "Hello" "World!")))
    (t/is (= "Hello, World!" (dmm/concat-with-space "Hello," "World!")))))

(t/deftest search-text-test
  (t/testing "A string can be sliced to the last n words"
    (let [test-text "If on a winter's night a traveler"]
      (t/is (thrown? clojure.lang.ArityException (dmm/search-text test-text 0)))
      (t/is (= (dmm/search-text test-text 1) "traveler"))
      (t/is (= (dmm/search-text test-text 2) "a traveler"))
      (t/is (= (dmm/search-text test-text 3) "night a traveler"))
      (t/is (= (dmm/search-text test-text 4) "winter's night a traveler"))
      (t/is (= (dmm/search-text test-text 7) test-text))
      (t/is (= (dmm/search-text test-text 8) test-text)))))

(t/deftest string-to-sliding-window-test
  (t/testing "A string may be sliced into multiple text/transition windows"
    (let [test-text "It was a pleasure to burn."]
      (t/is (= (dmm/string-to-sliding-window test-text 0) ['("It") '("was") '("a") '("pleasure") '("to") '("burn.")]))
      (t/is (= (dmm/string-to-sliding-window test-text 1) ['("It" "was") '("was" "a") '("a" "pleasure") '("pleasure" "to") '("to" "burn.")]))
      (t/is (= (dmm/string-to-sliding-window test-text 5) ['("It" "was" "a" "pleasure" "to" "burn.")]))
      (t/is (empty? (dmm/string-to-sliding-window test-text 6))))))

(t/deftest sentence-predicate-test
  (t/testing "Ensure sentence predicates accurately track English behavior"
    (t/is (dmm/starts-sentence? ["A" "screaming" "comes" "across" "the" "sky."]))
    (t/is (not (dmm/starts-sentence? ["124" "was" "spiteful."])))
    (t/is (not (dmm/starts-sentence? ["—Money" "." "." "." "in" "a" "voice" "that" "rustled."])))
    (t/is (not (dmm/starts-sentence? ["the" "moment" "one" "learns" "English," "complications" "set" "in."])))
    (t/is (dmm/sentence-ended? "Dr. Weiss, at forty, knew that her life had been ruined by literature"))
    (t/is (dmm/sentence-ended? "The story so far: In the beginning the Universe was created."))
    (t/is (dmm/sentence-ended? "Call me Ishmael."))
    (t/is (dmm/sentence-ended? "Where now? Who now? When now?"))
    (t/is (dmm/sentence-ended? "Stop! cried the groaning old man at last, Stop! I did not drag my father beyond this tree"))
    (t/is (not (dmm/sentence-ended? "The ting go skraaa")))
    (t/is (not (dmm/sentence-ended? "All this happened, more or less-")))))

(t/deftest main-test
  (t/testing "Ensure all helper functions are wired correctly"
    (let [window-size 3
          data (dmm/load-data! "./test/dj_marky_markov/test.txt" window-size)
          result (dmm/write-sentences data window-size (inc (rand-int 100)))]
      (t/is (coll? result))
      (t/is (seq result))
      (t/is (every? string? result))
      (t/is (thrown? Exception (dmm/load-data! "./test/dj_jazzy_jazzof/fake.txt" window-size))))))
