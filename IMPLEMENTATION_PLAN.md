# Implementation Plan — Event-Driven E-Commerce Microservices

This document outlines the detailed architectural design, tech stack decisions, module decomposition, and step-by-step implementation strategy for building an enterprise-grade **Event-Driven E-Commerce Microservices System**.

---

## 🏛️ Architecture Overview

The system is designed following the **Database-per-Service** design pattern and **Event-Driven Architecture (EDA)** to achieve high scalability, fault isolation, and loose coupling.

```
                  ┌──────────────────────┐
                  │    Client (Web/App)  │
                  └──────────┬───────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │     API Gateway      │ (Spring Cloud Gateway, Global JWT Filter,
                  └──────────┬───────────┘  Rate Limiting)
                             │
       ┌─────────────────────┼─────────────────────┬─────────────────────┐
       │ HTTP/REST           │ HTTP/REST           │ HTTP/REST           │ HTTP/REST
       ▼                     ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ User Service │      │ProductService│      │ Order Service│      │ SearchService│
│ (Auth/JWT)   │      │ (Redis Cache)│      │(Outbox/Saga) │      │(Elasticsearch)
└──────┬───────┘      └──────┬───────┘      └──────┬───────┘      └──────┬───────┘
       │ DB                  │ DB                  │ DB                  │ ES Index
  [PostgreSQL]          [PostgreSQL]          [PostgreSQL]         [Elasticsearch]
                             │                     │
                             └──────────┬──────────┘
                                        │ Events (ProductCreated, OrderCreated, etc.)
                                        ▼
                            ┌──────────────────────┐
                            │    Apache Kafka      │ (Event Bus)
                            └──────────┬───────────┘
                                       │
            ┌──────────────────────────┼──────────────────────────┐
            ▼                          ▼                          ▼
   ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
   │Inventory Service│        │ Payment Service │        │Notification Svc │
   └────────┬────────┘        └────────┬────────┘        └────────┬────────┘
            │ DB                       │ DB                       │ DB
       [PostgreSQL]               [PostgreSQL]               [PostgreSQL]
```

---

## 📦 Service Breakdown & Responsibilities

| Service | Primary Tech / Tools | Primary Responsibilities | Storage |
|---|---|---|---|
| **`api-gateway`** | Spring Cloud Gateway, Reactive JWT Filter, Redis | Entry point, JWT token validation, Route dispatching, Rate limiting | Redis |
| **`user-service`** | Spring Boot, Spring Security, JWT (JJWT), Validation | User registration, authentication, JWT issuing (access/refresh), Profile management | PostgreSQL (`user_db`) |
| **`product-service`** | Spring Boot, Spring Data JPA, Redis Cache, Kafka Producer | Product catalog CRUD, category management, Redis cache-aside reads, publishing product events | PostgreSQL (`product_db`) + Redis |
| **`search-service`** | Spring Boot, Spring Data Elasticsearch, Kafka Consumer | Ingests product events from Kafka, indexes data in Elasticsearch, provides fuzzy search & multi-field filtering APIs | Elasticsearch |
| **`inventory-service`** | Spring Boot, Spring Data JPA, Kafka Producer/Consumer | Manages stock levels, pessimistic/optimistic locking for concurrent stock reservations, emits inventory events | PostgreSQL (`inventory_db`) |
| **`order-service`** | Spring Boot, Spring Data JPA, Transactional Outbox, Kafka | Manages order lifecycle, Saga orchestrator for checkout flow (Order -> Inventory -> Payment) | PostgreSQL (`order_db`) |
| **`payment-service`** | Spring Boot, Spring Data JPA, Kafka Producer/Consumer | Simulates payment gateway processing, emits `PaymentSuccess` / `PaymentFailed` events | PostgreSQL (`payment_db`) |
| **`notification-service`** | Spring Boot, JavaMail, Kafka Consumer | Listens to system events and sends asynchronous notifications/emails to users | PostgreSQL (`notification_db`) |

---

## 🛠️ Step-by-Step Execution Plan

### Step 0: Maven Multi-Module Project Structure & Docker Setup
- Refactor the workspace into a clean Maven multi-module layout:
  - `ecommerce-parent` (Root POM)
    - `common-dto` (Shared Records, DTOs, Event payloads, Exceptions)
    - `api-gateway`
    - `user-service`
    - `product-service`
    - `search-service`
    - `inventory-service`
    - `order-service`
    - `payment-service`
    - `notification-service`
- Create `docker-compose.yml` defining services for:
  - PostgreSQL containers (per service / logically separated databases)
  - Kafka + Zookeeper (or KRaft mode)
  - Redis
  - Elasticsearch + Kibana
  - Zipkin

### Step 1: User & Authentication Service (`user-service`)
- Build User Domain (`User` entity with `id`, `email`, `password`, `roles`, `createdAt`).
- Implement `UserRepository` with Spring Data JPA.
- Create DTO records (`RegisterRequest`, `LoginRequest`, `AuthResponse`, `UserResponse`).
- Configure Spring Security & BCrypt Password Encoder.
- Build `JwtTokenProvider` to generate and validate JWT tokens (Access token expiration: 15 mins, Refresh token: 7 days).
- Expose REST endpoints: `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/users/me`.
- Implement `@RestControllerAdvice` for global exception handling.

### Step 2: Product Service with Redis Caching (`product-service`)
- Build Product & Category entities and JPA Repositories.
- Implement Product Service with Redis caching (`@Cacheable(value = "products", key = "#id")`, `@CacheEvict`).
- Implement Kafka Producer to emit `ProductCreatedEvent` & `ProductUpdatedEvent` on topic `product-events`.

### Step 3: Elasticsearch Search Service (`search-service`)
- Configure Spring Data Elasticsearch connection.
- Create `ProductDocument` mapped to Elasticsearch index `products`.
- Implement Kafka Consumer for `product-events` to update/index products into Elasticsearch asynchronously.
- Provide REST APIs `/api/v1/search/products` supporting keyword search, price range filtering, category filtering, and pagination.

### Step 4: Inventory Service (`inventory-service`)
- Build Inventory entity (`productId`, `quantity`, `reservedQuantity`, `version` for optimistic locking).
- Implement stock reservation logic with concurrency handling.
- Subscribe to `order-events` via Kafka to reserve stock upon order placement.

### Step 5: Order Service & Event-Driven Saga (`order-service`)
- Build Order & OrderItem entities (`orderId`, `userId`, `totalAmount`, `status`).
- Implement **Transactional Outbox Pattern** to write order state and outbox event in a single database transaction.
- Setup Outbox Event Publisher to send messages to Kafka `order-events`.
- Manage Saga state machine: `CREATED` -> `INVENTORY_RESERVED` -> `PAYMENT_COMPLETED` -> `CONFIRMED` (or `CANCELLED` on failure).

### Step 6: Payment Service (`payment-service`)
- Process payment for reserved orders.
- Emit `PaymentSuccessEvent` or `PaymentFailedEvent` to Kafka `payment-events`.

### Step 7: Notification Service (`notification-service`)
- Consume Kafka events across all topics (`UserRegisteredEvent`, `OrderConfirmedEvent`, `PaymentFailedEvent`).
- Send simulated asynchronous email notifications.

### Step 8: API Gateway (`api-gateway`)
- Setup Spring Cloud Gateway.
- Implement `JwtAuthenticationFilter` (Reactive `GatewayFilterFactory`) to inspect `Authorization: Bearer <token>`, parse claims, and pass `X-User-Id` & `X-User-Roles` headers to downstream microservices.
- Define routing rules in `application.yml` for `/api/v1/auth/**`, `/api/v1/products/**`, `/api/v1/orders/**`, etc.

---

## 🔒 Coding & Quality Standards

- **Java 21 & Spring Boot 3**: Use modern Java features (records, pattern matching, var).
- **Constructor Injection Only**: Pure dependency injection via `final` fields and `@RequiredArgsConstructor` / explicit constructors — NO `@Autowired` field injection.
- **Layered Architecture**: Enforce strict separation: `Controller` -> `Service` -> `Repository`. No business logic in Controllers.
- **Data Transfer Objects (DTOs)**: All request/response contracts defined as immutable Java `record`s.
- **Design Patterns Applied**:
  - **Saga Pattern**: Orchestrating multi-service checkout transactions.
  - **Transactional Outbox Pattern**: Dual writing database and event bus reliably.
  - **Cache-Aside Pattern**: Redis caching in Product service.
  - **Factory / Builder Pattern**: Constructing complex DTOs and domain events.
