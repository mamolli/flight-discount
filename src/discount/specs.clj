(ns flight.specs)

;; Empty file/docs do not plan on adding entities below as validation for now
;; In the tasks it is implied that rules might change, so I choose to wait and see
;; Thus the maps below are reference only - to better understand data shapes

;; tenant
;; {:tenant/id
;;  :tenant/group
;;  :tenant/birthday}

;; flight 
;; {:flight/ID
;;  :flight/from
;;  :flight/to
;;  :flight/price
;;  :flight/days
;;  :flight/hour
;;  :flight/departure}

;; booking
;; {:booking/price
;;  :booking/tenant
;;  :booking/flight
;;  :booking/rules []}

;; rule
;; {:rule/name 
;;  :rule/update-fn
;;  :rule/predicate-fn))