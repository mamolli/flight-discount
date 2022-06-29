(ns basic-test
  (:require [clojure.test :refer [deftest is]]
            [tick.core :as t]
            [discount.core :refer [buy-flight]]))

(def dates {:thursday (t/new-date 2022 6 30) 
            :other    (t/new-date 2022 6 28)})

(def tenants
 {:thursday-group-A
    {:tenant/group :A :tenant/birthday (:thursday dates)}
  :thursday-group-B
    {:tenant/group :B :tenant/birthday (:thursday dates)}
  :other-group-A
    {:tenant/group :A :tenant/birthday (:other dates)}})

(def flights
 {:africa-thursday-50
   {:flight/ID "x" :flight/price 50 :flight/to "Africa" :flight/departure (:thursday dates)}
  :africa-thursday-21
   {:flight/ID "x" :flight/price 21 :flight/to "Africa" :flight/departure (:thursday dates)}
  :africa-other-50
   {:flight/ID "x" :flight/price 50 :flight/to "Africa" :flight/departure (:other dates)}
  :europe-thursday-50
   {:flight/ID "x" :flight/price 50 :flight/to "Europe" :flight/departure (:thursday dates)}
  :europe-other-50
   {:flight/ID "x" :flight/price 50 :flight/to "Europe" :flight/departure (:other dates)}})

;; helper for quick verifications
(defn check-price+rules? [booking price rules-count]
  (and (= price (:booking/price booking))
       (= rules-count (-> (:booking/rules booking) (count)))))


;; I would make a proper matrix here {[:x :y] [price count-rules]} and run it all
;; but working under limited time, so just hinting
(deftest basic
  (is (true? (-> (buy-flight (:africa-thursday-50 flights)
                             (:thursday-group-A   tenants)) 
                 (check-price+rules? 40 2))))
  (is (true? (-> (buy-flight (:africa-thursday-21 flights)
                             (:thursday-group-A   tenants)) 
                 (check-price+rules? 21 0))))
  (is (true? (-> (buy-flight (:africa-thursday-50 flights)
                             (:thursday-group-B   tenants)) 
                 (check-price+rules? 40 0))))
  (is (true? (-> (buy-flight (:europe-other-50 flights)
                             (:thursday-group-B   tenants)) 
                 (check-price+rules? 50 0))))
  (is (true? (-> (buy-flight (:europe-thursday-50 flights)
                             (:thursday-group-A   tenants)) 
                 (check-price+rules? 45 1))))
  (is (true? (-> (buy-flight (:europe-thursday-50 flights)
                             (:thursday-group-B   tenants)) 
                 (check-price+rules? 45 0))))
  (is (true? (-> (buy-flight (:europe-other-50 flights)
                             (:thursday-group-A   tenants)) 
                 (check-price+rules? 50 0))))
  ;; to also test if the helper function works correctly:
  (is (false? (-> (buy-flight (:europe-thursday-50 flights)
                              (:other-group-A   tenants)) 
                  (check-price+rules? 57 11)))))

