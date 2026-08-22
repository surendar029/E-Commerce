# Project Progress Tracker — E-Commerce Microservices

**Architecture:** Event-Driven Microservices Architecture  
**Tech Stack:** Java 21, Spring Boot 3+, Spring Cloud Gateway, PostgreSQL (Database-per-Service), Apache Kafka, Redis, Elasticsearch, Docker & Docker Compose, Resilience4j, Zipkin/Micrometer  
**Role / Persona:** Senior Java Backend Engineer (Mentor Mode)

---

## 🎯 Current Task
- **Phase 5: Inventory Service (`inventory-service`)**

---

## 📋 Phase Roadmap & Task Breakdown

### Phase 0: Workspace & Architecture Infrastructure Blueprint
- [ ] Set up Multi-Module Maven structure (Parent `pom.xml` managing common versions, dependencies, and plugin management).
- [x] Create `docker-compose.yml` for local infrastructure (PostgreSQL instances, Kafka + KRaft, Redis, Elasticsearch, Zipkin).
- [ ] Establish common shared library module (`common-dto` / `common-events`) for shared DTO records, custom exceptions, and event contracts.

### Phase 1: Auth & User Service (`user-service`)
- [x] Database setup (`user_db` PostgreSQL).
- [x] User Domain Entity & Repository layer.
- [x] DTO records (`RegisterRequest`, `LoginRequest`, `AuthResponse`, `ErrorResponse`).
- [x] Password Encoding (BCrypt) & User Registration logic with validations.
- [x] JWT Utility component (Access token generation with embedded claims, signing, single-pass parsing).
- [x] `UserService` & `UserController` (REST Endpoints for register, login/authenticate).
- [x] Stateless `JwtAuthenticationFilter` integration with Spring Security.
- [x] Custom Exception Handler (`@RestControllerAdvice`).
- [x] Code audit & security review fixes.
- [ ] Unit & Integration tests for Auth flows (Deferred to final integration stage).

### Phase 2: Product Service & Redis Caching (`product-service`)
- [x] Database setup (`product_db` PostgreSQL).
- [x] Domain Entities (`Product`, `Category`) & Repositories.
- [x] Product DTO records & Mapper.
- [x] Redis Cache configuration (Cache-Aside pattern using `@Cacheable`, `@CacheEvict`).
- [x] Product CRUD Service & Controller APIs.
- [x] Kafka Producer integration: Publish `ProductCreatedEvent` & `ProductUpdatedEvent`.

### Phase 3: Event-Driven Kafka Infrastructure (`event-bus`)
- [x] Kafka Topic configuration (`product-events` topic with partition/replica settings).
- [x] Custom Kafka Producer & Consumer serialization configurations (JSON with ErrorHandlingDeserializer).
- [x] Idempotent Consumer & Error Handling (Dead Letter Topic - DLT strategy with BackOff and DeadLetterPublishingRecoverer).

### Phase 4: Elasticsearch Search Service (`search-service`)
- [x] Elasticsearch connection & Index Mapping setup (`products` index, `ProductDocument` with text & keyword analyzers).
- [x] Kafka Consumer listening to product events -> Syncing data into Elasticsearch (`ProductEventConsumer` handling `CREATED`, `UPDATED`, `DELETED`).
- [x] Search Service & REST Controller for full-text search, fuzzy search, category filtering, price range, sorting, and pagination (`ProductSearchService` & `ProductSearchController`).

### Phase 5: Inventory Service (`inventory-service`)
- [ ] Database setup (`inventory_db` PostgreSQL).
- [ ] Domain Entity (`Inventory`) & Optimistic Locking / Pessimistic Locking for stock concurrency.
- [ ] Stock Reservation API & Stock Replenishment logic.
- [ ] Kafka Consumer: Listen for `OrderCreatedEvent` -> Reserve Stock -> Publish `InventoryReservedEvent` or `InventoryReservationFailedEvent`.

### Phase 6: Order Service & Saga Orchestration (`order-service`)
- [ ] Database setup (`order_db` PostgreSQL).
- [ ] Domain Entities (`Order`, `OrderItem`, `OrderStatus`) & Repositories.
- [ ] Transactional Outbox Pattern setup for reliable event publishing.
- [ ] Order Creation API (State: `PENDING_INVENTORY`).
- [ ] Saga Orchestration logic listening to `Inventory` and `Payment` events.

### Phase 7: Payment Service (`payment-service`)
- [ ] Database setup (`payment_db` PostgreSQL).
- [ ] Payment processing domain logic & Mock Payment Gateway adapter.
- [ ] Kafka Consumer: Listen for `InventoryReservedEvent` -> Process Payment -> Publish `PaymentSuccessEvent` or `PaymentFailedEvent`.

### Phase 8: Notification Service (`notification-service`)
- [ ] Kafka Consumers for key events (`UserRegisteredEvent`, `OrderConfirmedEvent`, `PaymentFailedEvent`).
- [ ] Email/Notification service handler with template processing.

### Phase 9: API Gateway Service (`api-gateway`)
- [ ] Spring Cloud Gateway configuration.
- [ ] Custom Reactive Gateway Filter for JWT authentication & authorization header forwarding.
- [ ] Route configurations for microservices (`user-service`, `product-service`, `order-service`, etc.).
- [ ] Global Rate Limiting filter (Redis RateLimiter).
- [ ] Gateway CORS & Global Error Handling setup.

### Phase 10: System Integration, Resilience & Deployment
- [ ] Resilience4j Circuit Breakers & Fallback mechanisms across HTTP/REST interactions.
- [ ] Distributed Tracing with Micrometer Tracing & Zipkin.
- [ ] End-to-End Docker Compose build and verification for all microservices.

---

## 🔍 Completed Tasks
- [x] Infrastructure `docker-compose.yml` with PostgreSQL (auto-init `user_db`, `product_db`, `inventory_db`, `order_db`, `payment_db`, `notification_db`), Kafka (KRaft mode), Redis, Elasticsearch, and Zipkin.
- [x] Phase 2: `product-service` with PostgreSQL setup, domain records, Redis Cache-Aside pattern, CRUD REST APIs, and Kafka event producers.
