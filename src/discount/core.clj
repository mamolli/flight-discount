(ns discount.core
  (:require [tick.core :as t]
            [clojure.pprint :refer [pprint]]
            [discount.booking :as booking])
  (:gen-class))

;; go to specs.clj to see datashapes

(defn ->rule
  "Takes name, predicate-fn, update-fn and returns map representing rule.
    predicate-fn: function that takes booking map 
      and returns true/false if the rule should match
    update-fn: function that takes booking map
      when predicate-fn is true and returns modified booking map"
 ([predicate-fn? update-fn]
  (->rule "Ad-hoc unnamed rule" predicate-fn? update-fn))
 ([name predicate-fn? update-fn]
  {:rule/name name ;; to better identify rules
   :rule/predicate-fn predicate-fn?
   :rule/update-fn update-fn})) 

(def rules 
  [(->rule "Flight departs on tenants birthday" 
            booking/flight-departs-on-tenants-birthday?
            booking/decrease-price-by-5)
   (->rule "Flight to Africa on Thursday" 
            (fn [booking] (and (booking/flight-to-africa? booking)
                               (booking/flight-departs-thursday? booking)))
            booking/decrease-price-by-5)])

;; if booking price gets below 20 discount rules are no longer applied
;; order of rules becomes important, rules on top will be applied first
;; this is why I moved birthday to first in rules, as it seems more a personal discount
(defn skip-rule? [{:booking/keys [price]}]
  "Predicate function for not applying rule(s) to booking, returns true/false"
  (not (<= 20 price)))

(defn record-rules? [{:booking/keys [tenant]}]
  "Predicate function for not persisting applied rules/discounts, returns true/false"
  (= (:tenant/group tenant) :A)) ;; group :A is a group that allows recording of applied rules 

(def reduce-booking-fn
  "Reducing function is booking accumulator 
   and its reduced by sequence of rules, returns a discounted booking"
  (fn [booking {:rule/keys [name predicate-fn update-fn] :as rule}]
    (let [predicate? (predicate-fn booking) 
          booking*   (cond-> booking predicate? (update-fn))]
      (if (skip-rule? booking*) 
        booking ;; we could (reduced booking) here, because we always decrease by 5 - but that might change
        (if (record-rules? booking*) 
          (cond-> booking* predicate? (update :booking/rules conj rule))
          (assoc booking* :booking/rules nil))))))

;; extra arrity added for better testing capabilities (not used at the moment)
(defn buy-flight 
 "Takes flight and tenant and returns booking map with discounts applied"
 ([flight tenant]
  (buy-flight flight tenant rules))
 ([flight tenant rules]
  (let [booking (booking/->booking flight tenant)]
    (reduce reduce-booking-fn booking rules))))

(defn -main []
  (println "Example run:\n")
  (let [flight {:flight/ID "x" :flight/price 33 :flight/to "Africa" :flight/departure (t/new-date 2022 6 30)}
        tenant {:tenant/group :A :tenant/birthday (t/new-date 2022 6 30)}
        booking (booking/->booking flight tenant)]
    (println "- Booking before discounts(original):")
    (pprint booking)
    (println)
    (println "- Booking after discounts:")
    (pprint (buy-flight flight tenant))))


(comment
  (t/date (t/now))
  (t/day-of-week (t/now))
  (def flight {:flight/ID "x" :flight/price 33 :flight/to "Africa" :flight/departure (t/new-date 2022 6 30)})
  (def tenant {:tenant/group :A :tenant/birthday (t/new-date 2022 6 30)})
  (def booking (booking/->booking flight tenant))
  (booking/decrease-price-by-5 booking)
  (booking/flight-departs-thursday? booking)
  (buy-flight flight tenant))



  

