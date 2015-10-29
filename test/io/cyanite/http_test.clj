(ns io.cyanite.http-test
  (:require [io.cyanite.http :refer :all]
            [clojure.test :refer :all])
  (:import clojure.lang.ExceptionInfo))

(def maxDataPoints 1680)
(def fifteen_minutes (* 60 15))
(def one_hour (* 60 60))
(def six_hours (* 6 one_hour))
(def one_day (* 60 60 24))
(def seventy_days (* one_hour 1680))
(def one_week (* one_day 7))
(def two_weeks (* one_day 7 2))
(def four_weeks (* two_weeks 2))
(def six_months (* one_day 30 6))
(def one_year (* one_day 366))
(def rollups [{:rollup 10 :period 1680 :ttl 16800 :maxDataPoints 1680}
              {:rollup 60 :period 1680 :ttl 100800 :maxDataPoints 1680}
              {:rollup 300 :period 1680 :ttl 504000 :maxDataPoints 1680}
              {:rollup 900 :period 35136 :ttl 31622400 :maxDataPoints 1680}
              {:rollup one_hour :period 1680 :ttl seventy_days :maxDataPoints 1680}
              {:rollup six_hours :period 1680 :ttl 36288000 :maxDataPoints 1680}
              ])

(deftest find-best-rollup-test
  (testing ""
    (is (= 10 (get (find-best-rollup (str (- (now) 40)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 10 (get (find-best-rollup (str (- (now) 16800)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 60 (get (find-best-rollup (str (- (now) 16801)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 60 (get (find-best-rollup (str (- (now) 100800)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 300 (get (find-best-rollup (str (- (now) 100801)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 300 (get (find-best-rollup (str (- (now) 504000)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 900 (get (find-best-rollup (str (- (now) 504001)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 900 (get (find-best-rollup (str (- (now) 1512000)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 3600 (get (find-best-rollup (str (- (now) 1512001)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 3600 (get (find-best-rollup (str (- (now) 6048000)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 21600 (get (find-best-rollup (str (- (now) 6048001)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 21600 (get (find-best-rollup (str (- (now) 31622400)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 21600 (get (find-best-rollup (str (- (now) 31622401)) (str (now)) rollups) :rollup))))
  (testing ""
    (is (= 21600 (get (find-best-rollup (str (- (now) 36288001)) (str (now)) rollups) :rollup))))

  (testing "zoom in to recent 2 week window shows 15 minute resolution"
    (is (= 900 (get (find-best-rollup (str (- (now) four_weeks)) (str (- (now) two_weeks)) rollups) :rollup))))
  (testing "zoom in to recent 4 week window shows 1 hour resolution"
    (is (= 3600 (get (find-best-rollup (str (- (now) four_weeks four_weeks)) (str (- (now) four_weeks)) rollups) :rollup))))
  (testing "zoom in to old 2 week window shows 15 minute resolution"
    (is (= 900 (get (find-best-rollup (str (- (now) six_months two_weeks)) (str (- (now) six_months)) rollups) :rollup))))
  (testing "zoom in to old 4 week window shows 6 hour resolution when 1 hour resolution is unavailable"
    (is (= 21600 (get (find-best-rollup (str (- (now) six_months four_weeks)) (str (- (now) six_months)) rollups) :rollup))))
  (testing "zoom in to old 4 week window shows 6 hour resolution when 1 hour resolution is partially unavailable"
    (is (= 21600 (get (find-best-rollup (str (- (now) seventy_days two_weeks)) (str (+ (- (now) seventy_days)) two_weeks) rollups) :rollup))))

  (testing "to value defaults to now"
    (is (= 900 (get (find-best-rollup (str (- (now) one_week)) nil rollups) :rollup))))
  )
