# Hotel Booking System

A backend service for a hotel booking platform built with **Java 21 + Spring Boot 3.2 + H2 (in-memory)**.

---

## How to Build and Run

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Clone / extract the project
cd Hotel-Booking-System

# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
```

The service starts on **http://localhost:8080**.  
H2 console available at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:hoteldb`).

### Run Tests
```bash
mvn test
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/owners` | Register a property owner |
| GET | `/api/owners/{id}` | Get owner by ID |
| POST | `/api/owners/{ownerId}/properties` | Add a property to an owner |
| GET | `/api/owners/{ownerId}/properties` | List owner's properties |
| GET | `/api/search?city=&checkInDate=&checkOutDate=&numGuests=&minPrice=&maxPrice=&amenities=&minStarRating=` | Search properties |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings/{id}` | Get booking by ID |
| GET | `/api/bookings?email=` | Get bookings by guest email |
| PUT | `/api/bookings/{id}/check-in` | Check in |
| PUT | `/api/bookings/{id}/check-out` | Check out |
| DELETE | `/api/bookings/{id}` | Cancel booking |
| POST | `/api/bookings/{bookingId}/payment` | Pay for a booking |
| GET | `/api/bookings/{bookingId}/payment` | Get payment details |

---

## Key Design Decisions

### 1. Single Owner model for single and multi-property owners
An `Owner` always has a list of `Property` objects. A single-property owner is simply an owner with one property — no separate class or flag needed.

### 2. Pluggable Search Filters (Open/Closed Principle)
Every filter (`CityFilter`, `LocalityFilter`, `StarRatingFilter`, `AmenityFilter`, `PriceRangeFilter`, `AvailabilityFilter`, `GuestCapacityFilter`) implements `SearchFilter`. `SearchServiceImpl` auto-collects all Spring beans of that type. Adding a new filter = implementing the interface + annotating with `@Component`. `GuestCapacityFilter` activates only when dates are absent (when dates are present, `AvailabilityFilter` already handles capacity — no double-filtering).

### 3. Payment Gateway Abstraction
`PaymentGateway` interface with `supports(PaymentMethod)`. `PaymentGatewayResolver` picks the right implementation at runtime. Currently mocked: `CardPaymentGateway`, `UpiPaymentGateway`, `WalletPaymentGateway`.

### 4. Pluggable Cancellation Policies
`CancellationPolicy` interface drives refund logic. `CancellationPolicyResolver` selects the right policy based on days until check-in (≥7 days → full refund, 2–6 days → 50%, <2 days → none).

### 5. Booking State Machine
Transitions are enforced inside `Booking.transitionTo()`:
`PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT`
`PENDING / CONFIRMED → CANCELLED`

Invalid transitions throw `InvalidStateTransitionException` (HTTP 422).

### 6. Pluggable Pricing Strategy
`PricingStrategy` interface with `isApplicable(room, dateRange)` + `calculateTotalPrice(room, dateRange)`. `PricingStrategyResolver` picks the first applicable strategy (ordered via `@Order`). Shipped with `WeekendSurchargePricingStrategy` (25% surcharge on Sat/Sun for SUITE/PENTHOUSE, `@Order(1)`) and `FlatNightlyPricingStrategy` (default fallback, `@Order(10)`). New strategies plug in with `@Component`.

### 7. Concurrency / Double-booking Prevention
- `RoomRepository.findAvailableRooms` uses `@Lock(PESSIMISTIC_WRITE)` to lock rows during availability check.
- `BookingServiceImpl.createBooking` is `synchronized` as an additional safeguard within a single JVM.
- Room entity carries `@Version` for optimistic locking fallback.

---

## Assumptions

- Authentication/authorisation is out of scope; all endpoints are open.
- A "guest" is identified by name + email (no separate User entity required).
- Payment is always mocked to succeed; real gateway integration is behind the `PaymentGateway` interface.
- Pricing is flat per-night; dynamic pricing can be added via a `PricingStrategy` interface (bonus extension point).
- Currency is INR by default.

---

## What I Would Do With More Time

- JWT-based auth with role separation (GUEST / OWNER / ADMIN).
- Idempotency keys stored in DB to prevent duplicate payment processing.
- Dynamic pricing strategy (pluggable via `PricingStrategy` interface).
- Pagination on search results.
- OpenAPI / Swagger documentation.
- Integration tests covering the full HTTP layer with `@SpringBootTest + MockMvc`.
