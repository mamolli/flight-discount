(ns discount.booking
  (:require [tick.core :as t]))

(defn ->booking
  "Takes flight and tenant maps and returns initial booking map."
  [{:flight/keys [price] :as flight} tenant]
  {:booking/price price
   :booking/flight flight
   :booking/tenant tenant
   :booking/rules []})

;; Discount predicates

(defn flight-to-fn? [to]
  (fn [booking]
    (= to (get-in booking [:booking/flight :flight/to]))))

(defn flight-departs-weekday-fn? [day]
  (fn [booking]
    (= day (-> (get-in booking [:booking/flight :flight/departure])
               (t/day-of-week)))))

(defn flight-departs-on-tenants-birthday? 
  [booking]
  (let [flight-date          (-> booking (get-in [:booking/flight :flight/departure]) (t/date))
        tenant-birthday-date (-> booking (get-in [:booking/tenant :tenant/birthday]) (t/date))]
    (= flight-date tenant-birthday-date)))

(def flight-to-africa? 
  (flight-to-fn? "Africa"))

(def flight-departs-thursday?
  (flight-departs-weekday-fn? (t/day-of-week "THURSDAY")))

;; Discount updates

(defn decrease-price-by-5 [booking]
  (update booking :booking/price (fn [price] (- price 5))))

;; if discounts were more various this how I would approach the
;; (defn decrese-price-by-fn [discount-value]
;;   (fn [booking] 
;;     (update booking :booking/price (fn [price] (- price discount-value)))))

