# Flight discount system

In this repo you will find a showcase application (in Clojure) of rule based system for applying discounts to airline tickets. 

More info on requirements and description in: `./assignment.md`

## Dev run
Dependencies:
  - Typical posix x86 environment: `macos/linux/WSL2(windows)`
  - Base dependency: `clojure 1.10.3`, you need to have `clj` binary available
  - Run app (it will executed over one example and print before and after): `clj -M -m discount.core`
  - Run test (covers a couple more cases): `clj -X:test`

## Derived Assumptions / Unclear

Normally I would just consult with team/product owner/users on any of the below. However here I have to just take a stand on couple of important issues below.

- Assignment suggests entrypoint to look like `buyFlight(FlightId flightId, TenantId tenantId)`, I will diverge from this entrypoint to be more in tune with clojure `(buy-flight flight tennant)`. This function takes a flight and tennant maps (with complete data) rather than just id. The system could be easily adapted to the id case, by adding a small wrapping function over entrypoint that would fetch data from source using ids.

- Adding additional data to Flight - `departure`. Tenant/buyer is purchasing an actual flight and this flight must have a concrete departure(datetime). Departure cannot be inferred from: 
  >contains the ID, the route from, to, hour and days of the week of departure.

- Also adding initial `price` to Flight. (to see more about structures used go: `./src/discount/specs.clj/`)

- Assuming discounts have atomic nature. They should only be applied in full or not all. Discounts do not apply partially. Discounts will be applied in order.
 
