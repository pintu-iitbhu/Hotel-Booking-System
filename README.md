# Hotel Booking System

This is a Spring Boot backend for a hotel booking platform. I built it as a machine-coding exercise, so the focus was on clean design and extensibility rather than feature completeness. It runs fully in-memory using H2, so there's nothing to set up beyond Java and Maven.

**Stack:** Java 21, Spring Boot 3.2, H2 (in-memory), Maven

Interactive API docs available at `http://localhost:8080/swagger-ui/index.html` once the service is running.

---

## Running the project

You'll need Java 21 and Maven 3.8+. Clone or extract the project and run:

```bash
mvn spring-boot:run
```

That's it. The service starts on `http://localhost:8080`. If you want to poke around the database, the H2 console is at `http://localhost:8080/h2-console` — use JDBC URL `jdbc:h2:mem:hoteldb`, username `sa`, no password.

To run the tests:

```bash
mvn test
```

---

## What the API looks like

**Owners and properties**
```
POST   /api/owners                          register an owner
GET    /api/owners/{id}
POST   /api/owners/{ownerId}/properties     onboard a property under an owner
GET    /api/owners/{ownerId}/properties
GET    /api/properties/{id}
```

**Discovery**
```
GET    /api/search?city=&locality=&checkInDate=&checkOutDate=
                  &numGuests=&minPrice=&maxPrice=&amenities=&minStarRating=
```
Any combination of those query params works — just pass the ones you care about.

**Booking lifecycle**
```
POST   /api/bookings                        create a booking (starts as PENDING)
GET    /api/bookings/{id}
GET    /api/bookings?email=
PUT    /api/bookings/{id}/check-in
PUT    /api/bookings/{id}/check-out
DELETE /api/bookings/{id}                   cancel + trigger refund
```

**Payment**
```
POST   /api/bookings/{bookingId}/payment    pay for a booking (PENDING → CONFIRMED on success)
GET    /api/bookings/{bookingId}/payment
```
To safely retry a payment without double-charging, pass a client-generated `Idempotency-Key` header:
```
Idempotency-Key: <your-uuid>
```
If the same key is submitted twice, the original payment is returned — the gateway is never called a second time.

---

## How it's structured

```
com.hotel.booking
├── domain/
│   ├── entity/       Owner, Property, Room, Booking, Payment
│   ├── enums/        BookingStatus, RoomType, Amenity, PaymentMethod, PaymentStatus
│   └── valueobject/  DateRange, Location
├── repository/       Spring Data JPA interfaces (H2 today, swappable tomorrow)
├── search/           SearchFilter interface + 7 implementations
├── payment/          PaymentGateway interface + resolver + 3 mocks
├── cancellation/     CancellationPolicy interface + resolver + 3 implementations
├── pricing/          PricingStrategy interface + resolver + 2 implementations
├── service/          interfaces + implementations
├── controller/       REST layer
├── dto/              request/response records
└── exception/        exception hierarchy + GlobalExceptionHandler
```

---

## Design decisions

**Owners and properties.** The requirement said to model a standalone property as a special case of a chain, not a separate path. So `Owner` just has a `List<Property>` — a single-property hotel is an owner with one property. No flags, no separate entity.

**Search filters.** I wanted new filters to be addable without touching existing code. Each filter implements a `SearchFilter` interface (`isApplicable` + `test`), and `SearchServiceImpl` just injects the full `List<SearchFilter>` from the Spring context — it doesn't know what filters exist. Adding a new one is literally one class with `@Component`. The seven shipped filters cover city, locality, star rating, amenities, price range, availability, and guest capacity. One thing worth noting: `GuestCapacityFilter` skips itself when dates are provided, because `AvailabilityFilter` already handles capacity in that case.

**Payment.** `PaymentGateway` is an interface with `process`, `refund`, and `supports`. A resolver picks the right implementation at runtime. Currently there are mocks for card, UPI, and wallet. Swapping in a real gateway means implementing the interface and registering it — nothing else changes.

**Cancellation policies.** Same pattern. `CancellationPolicyResolver` picks a policy based on how far out the check-in is: 7+ days gets a full refund, 2–6 days gets 50%, under 2 days gets nothing. If the refund call to the gateway fails, we throw rather than silently continue.

**Booking state machine.** State transitions live inside `Booking.transitionTo()` in the entity itself, not scattered across service methods. The valid paths are:
```
PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT
PENDING / CONFIRMED → CANCELLED
```
Anything else throws `InvalidStateTransitionException` and comes back as HTTP 422.

**Pricing (bonus).** `PricingStrategy` follows the same pluggable pattern. `PricingStrategyResolver` picks the first applicable strategy by `@Order`. Right now there's a weekend surcharge (25% on Sat/Sun for suites and penthouses) and a flat nightly fallback. New pricing rules are one class.

**Concurrency.** `findAvailableRooms` runs with `@Lock(PESSIMISTIC_WRITE)`, so the DB holds a row lock for the duration of the booking transaction. That prevents two concurrent requests from both seeing the same room as available. `Room` also has a `@Version` field as a secondary optimistic lock guard. Cancelled bookings are excluded from the availability query, so inventory releases itself automatically — no extra step needed.

**Payment idempotency.** `Payment` stores an `idempotencyKey` with a unique DB constraint. The client can pass an `Idempotency-Key` header on the payment endpoint. If the same key arrives twice, the existing payment is returned immediately — the gateway is never charged again. This is the right behaviour for retried requests in unreliable networks.

**A few JPA things I was deliberate about.** I avoided `@Data` on entities because Lombok's generated `hashCode` uses all fields, which triggers lazy collection loads and causes issues in JPA sets. Instead each entity uses `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` keyed on `id` only. I also disabled OSIV (`spring.jpa.open-in-view=false`) and used explicit `JOIN FETCH` queries on booking reads so lazy associations are fully loaded within the transaction rather than leaking into the web layer.

---

## Assumptions I made

- Auth is out of scope per the brief, so all endpoints are open.
- A guest is just a name + email — no separate User entity needed for this exercise.
- Payment gateways always go through the mock. The abstraction is there to make them swappable.
- Amounts are `BigDecimal`, assumed INR, no currency conversion.
- `Room.available = false` is an admin flag for things like maintenance. It's separate from date-based booking availability.

---

## What I'd tackle next

The main gap is **search scalability**. The current implementation loads all properties into memory and filters in Java — fine for the exercise, but it doesn't scale. The fix is JPA Specifications (`JpaSpecificationExecutor`) with pagination so filtering happens in the DB and results come back paged.

After that: integration tests with `@SpringBootTest` + `MockMvc`, and JWT-based auth with GUEST / OWNER / ADMIN roles.
