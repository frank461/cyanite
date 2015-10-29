(ns io.cyanite.config-test
  (:require [io.cyanite.config :refer :all]
            [clojure.test :refer :all])
  (:import clojure.lang.ExceptionInfo))

(deftest is-seconds-test
  (testing "15s becomes 15"
    (is (= 15 (to-seconds "15s"))))

  (testing "36m becomes 2160"
    (is (= 2160 (to-seconds "36m"))))

  (testing "2h becomes 7200"
    (is (= 7200 (to-seconds "2h"))))

  (testing "5d becomes 432000"
    (is (= 432000 (to-seconds "5d"))))

  (testing "2w becomes 1209600"
    (is (= 1209600 (to-seconds "2w"))))

  (testing "1y becomes 31536000"
    (is (= 31536000 (to-seconds "1y"))))

  (testing "unknown unit throws exception"
    (is (thrown? ExceptionInfo (to-seconds "2a")))))

(deftest convert-shorthand-test
  (testing "non-string is left alone"
    (is (= {:anything true} (convert-shorthand-rollup {:anything true}))))

  (testing "15s:6h is converted correctly"
    (is (= {:rollup 15
            :period 1440
            :ttl 21600} (convert-shorthand-rollup "15s:6h")))))

(deftest set-rollups-defaults-test
  (testing "rollup default values"
    (is (=
          [{:rollup 10 :period 200 :ttl 2000 :maxDataPoints 99}
           {:rollup 10 :period 200 :ttl 2000 :maxDataPoints 1680}
           {:rollup 10 :period 3000 :ttl 30000 :maxDataPoints 88}]
          (set-rollups-defaults 1680 [{:rollup 10 :period 200 :ttl 100 :maxDataPoints 99}
                                      {:rollup 10 :period 200 :ttl 77}
                                      {:rollup 10 :period 3000 :maxDataPoints 88}])
          ))))