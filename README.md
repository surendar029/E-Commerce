# E-Commerce Microservices

## Description

A **production-grade, event-driven E-Commerce microservices architecture** built with **Java 21**, **Spring Boot 3+**, **Apache Kafka**, **Redis**, and **Elasticsearch**. Designed for high scalability, fault tolerance, and asynchronous inter-service communication with resilient transaction processing, low-latency search caching, and robust distributed state management.

---

## Key Features

- **Kafka-Based Event Bus**: Decoupled services communicate through well-defined event topics
- **Redis Integration**: Fast shopping cart, session, and product catalog caching
- **Elasticsearch Index**: Lightning-fast product discovery with fuzzy matching
- **JWT-Based Security**: Stateless authentication with access/refresh token rotation
- **Horizontal Scaling**: Stateless services can scale independently
- **Circuit Breakers**: Prevent cascading failures with Resilience4j
 ---
 ## Tech Stack

* **Backend**: Java 21, Spring Boot 3, Spring Security, Spring Data JPA
* **Microservices & Messaging**: Spring Cloud, Apache Kafka
* **Database & Cache**: PostgreSQL, Redis, Elasticsearch
* **Resilience & Observability**: Resilience4j, Zipkin, Micrometer
* **DevOps & Tools**: Docker, Docker Compose, Maven, Postman
---
### 💾 Distributed Caching
- **Redis Integration**: Fast shopping cart, session, and product catalog caching
- **Cache-Aside Pattern**: Automatic cache invalidation on updates
- **TTL-Based Expiration**: Configurable cache lifetime management

### 🔍 Full-Text Product Search
- **Elasticsearch Index**: Lightning-fast product discovery with fuzzy matching
- **Multi-Field Search**: Search across product name, description, category, and tags
- **Aggregations & Facets**: Price ranges, category filtering, brand filtering
- **Real-Time Indexing**: Automatic sync from Kafka events

### 🔐 Authentication & Authorization
- **JWT-Based Security**: Stateless authentication with access/refresh token rotation
- **API Gateway Authentication**: Centralized JWT validation and header forwarding
- **Role-Based Access Control (RBAC)**: Authorization at gateway and service levels

### 📊 Saga Orchestration for Distributed Transactions
- **Order Checkout Flow**: Order → Inventory Reservation → Payment Processing → Confirmation
- **Compensating Transactions**: Automatic rollback on failures
- **Event Sourcing Ready**: Complete audit trail of all state changes

### 📈 Scalability & Resilience
- **Retry Policies**: Automatic exponential backoff for transient failures
- **Load Balancing**: Kafka partitioning for parallel processing
- **Health Checks**: Liveness and readiness probes for orchestration

### 🐳 Container-Ready Deployment
- **Docker Images**: Pre-configured for all services
- **Docker Compose**: Single-command local environment setup
- **Multi-Database Setup**: Auto-initialization of service-specific databases
- **Environment Configuration**: 12-factor app principles

### 📡 Distributed Tracing
- **Zipkin Integration**: End-to-end request tracing across services
- **Micrometer Instrumentation**: Automatic metric collection and reporting

---
## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Service Specifications](#service-specifications)
- [API Documentation](#api-documentation)
- [Event-Driven Architecture](#event-driven-architecture)
- [Development & Deployment](#development--deployment)
- [Project Roadmap](#project-roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## 🏛️ Architecture Overview

This project implements a **microservices architecture** following best practices for distributed systems:

```
                    ┌──────────────────────┐
                    │   Client (Web/App)   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │    API Gateway       │ (Spring Cloud Gateway, JWT, Rate Limiting)
                    └──────────┬───────────┘
                               │
        ┌──────────────────────┼──────────────────────┬──────────────────────┐
        │                      │                      │                      │
        ▼                      ▼                      ▼                      ▼
    ┌────────────┐        ┌────────────┐        ┌────────────┐        ┌────────────┐
    │User Service│        │Product Svc │        │Order Svc   │        │Search Svc  │
    │(Auth/JWT)  │        │(Redis)     │        │(Saga)      │        │(Elastic)   │
    └──────┬─────┘        └──────┬─────┘        └──────┬─────┘        └──────┬─────┘
           │ PostgreSQL          │ PostgreSQL          │ PostgreSQL          │ ES Index
           └──────────────────────┼───────────────────┘
                                  │
                      Events (Kafka Topics)
                                  │
         ┌────────────────────────┼────────────────────────┐
         ▼                        ▼                        ▼
    ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
    │Inventory Svc │        │Payment Svc   │        │Notification │
    │(Stock)       │        │(Payment)     │        │(Email)      │
    └──────────────┘        └──────────────┘        └──────────────┘
```

### Design Patterns

- **Event-Driven Architecture (EDA)**: Asynchronous communication via Apache Kafka for eventual consistency
- **Database-per-Service**: Each microservice has its own PostgreSQL database for data isolation
- **Saga Pattern**: Distributed transaction orchestration for complex workflows (e.g., Order → Inventory → Payment)
- **Transactional Outbox Pattern**: Reliable event publishing with dual-write consistency
- **Cache-Aside Pattern**: Redis for high-performance caching in product service
- **Circuit Breaker Pattern**: Resilience4j for fault tolerance and fallback mechanisms
- **CQRS Concept**: Search Service uses Elasticsearch for read optimization

---

## 🛠️ Technology Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Runtime** | Java | 21 | Modern language features (records, sealed classes, pattern matching) |
| **Framework** | Spring Boot | 3.3+ | Microservices foundation |
| **Cloud** | Spring Cloud | 2023+ | Service discovery, config, gateway |
| **API Gateway** | Spring Cloud Gateway | 4.x | Request routing, authentication, rate limiting |
| **Messaging** | Apache Kafka | 7.6+ | Event streaming & distributed messaging |
| **Cache** | Redis | 7.x | In-memory caching, session management |
| **Search Engine** | Elasticsearch | 9.4+ | Full-text search, faceted filtering |
| **Database** | PostgreSQL | 16 | Persistent relational storage (per-service) |
| **Container** | Docker & Docker Compose | Latest | Local development & deployment orchestration |
| **Tracing** | Zipkin | 3.x | Distributed request tracing |
| **Resilience** | Resilience4j | Latest | Circuit breakers, retry policies, bulkheads |
| **Build** | Maven | 3.8+ | Dependency management & multi-module builds |

---
## 📁 Project Structure

```
E-Commerce/
│
├── docker-compose.yml              # Multi-container orchestration for local development
├── docker/
│   └── postgres/
│       └── init-databases.sql      # Database initialization script
│
├── user-service/                   # 🔐 Authentication & User Management
│   ├── src/
│   │   └── main/java/
│   │       └── com/ecommerce/user/
│   │           ├── config/         # Spring Security, JWT configuration
│   │           ├── controller/     # REST endpoints (register, login, refresh)
│   │           ├── service/        # Business logic
│   │           ├── repository/     # JPA repositories
│   │           ├── entity/         # User domain entity
│   │           ├── dto/            # Request/Response records
│   │           ├── exception/      # Custom exceptions
│   │           └── filter/         # JWT authentication filter
│   └── pom.xml
│
├── product-service/                # 📦 Product Catalog & Caching
│   ├── src/
│   │   └── main/java/
│   │       └── com/ecommerce/product/
│   │           ├── config/         # Redis cache configuration
│   │           ├── controller/     # Product CRUD endpoints
│   │           ├── service/        # Product service with caching
│   │           ├── repository/     # Product, Category repositories
│   │           ├── entity/         # Product, Category entities
│   │           ├── dto/            # DTOs with mapper
│   │           ├── event/          # Kafka producer for product events
│   │           └── exception/      # Custom exceptions
│   └── pom.xml
│
├── search-service/                 # 🔍 Elasticsearch Full-Text Search
│   ├── src/
│   │   └── main/java/
│   │       └── com/ecommerce/search/
│   │           ├── config/         # Elasticsearch configuration
│   │           ├── controller/     # Search endpoint
│   │           ├── service/        # Search business logic
│   │           ├── document/       # ProductDocument for Elasticsearch
│   │           ├── event/          # Kafka consumer for product events
│   │           ├── dto/            # Search request/response DTOs
│   │           └── exception/      # Custom exceptions
│   └── pom.xml
│
├── inventory-service/              # 📊 Stock Management & Reservations
│   ├── src/
│   │   └── main/java/
│   │       └── com/ecommerce/inventory/
│   │           ├── config/         # Inventory configuration
│   │           ├── controller/     # Stock endpoints
│   │           ├── service/        # Stock reservation logic with concurrency control
│   │           ├── repository/     # Inventory repositories
│   │           ├── entity/         # Inventory entity with version for optimistic locking
│   │           ├── dto/            # DTOs
│   │           ├── event/          # Kafka consumer/producer
│   │           └── exception/      # Custom exceptions
│   └── pom.xml
│
├── order-service/                  # (Planned) 🛒 Order Lifecycle & Saga
│   ├── entity/                     # Order, OrderItem entities
│   ├── service/                    # Order service with Saga orchestration
│   ├── event/                      # Kafka consumer for inventory/payment events
│   └── outbox/                     # Transactional Outbox implementation
│
├── payment-service/                # (Planned) 💳 Payment Processing
│   └── service/                    # Mock payment gateway integration
│
├── notification-service/           # (Planned) 📧 Notifications & Email
│   └── service/                    # Email sending via event consumption
│
├── api-gateway/                    # (Planned) 🚪 API Gateway & Routing
│   └── config/                     # Spring Cloud Gateway routes, JWT filter
│
├── IMPLEMENTATION_PLAN.md          # Detailed technical design & roadmap
├── PROGRESS.md                     # Project completion tracking
└── .postman/                       # Postman API collection
```

---

## 🚀 Prerequisites

Ensure you have the following installed:

- **Java 21+**: [Download JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.8+**: [Download Maven](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose**: [Download Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Git**: For cloning the repository

### Verify Installation

```bash
java -version
# openjdk version "21.0.x" ...

mvn -version
# Apache Maven 3.8.x ...

docker --version
docker-compose --version
```

---

## ⚡ Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/surendar029/E-Commerce.git
cd E-Commerce
```

### 2. Start Infrastructure (Docker Compose)

```bash
# Start all services: PostgreSQL, Kafka, Redis, Elasticsearch, Zipkin
docker-compose up -d

# Verify all services are running
docker-compose ps
```

**Services & Access Points:**
- **PostgreSQL**: `localhost:5432` (user: `postgres`, password: `postgres`)
- **Kafka Broker**: `localhost:9092`
- **Redis**: `localhost:6379`
- **Elasticsearch**: `localhost:9200`
- **Zipkin**: `http://localhost:9411`

### 3. Build All Services

```bash
# Build user-service
cd user-service
mvn clean package
java -jar target/user-service-*.jar

# In a new terminal, build product-service
cd ../product-service
mvn clean package
java -jar target/product-service-*.jar

# In another terminal, build search-service
cd ../search-service
mvn clean package
java -jar target/search-service-*.jar

# In another terminal, build inventory-service
cd ../inventory-service
mvn clean package
java -jar target/inventory-service-*.jar
```

### 4. Verify Services Are Running

```bash
# Check user-service health
curl http://localhost:8080/actuator/health

# Check product-service health
curl http://localhost:8081/actuator/health

# Check search-service health
curl http://localhost:8082/actuator/health

# Check inventory-service health
curl http://localhost:8083/actuator/health
```

---

## 📡 Service Specifications

### 🔐 User Service (Port: 8080)

**Purpose**: Authentication, user registration, JWT token issuance

**Key Endpoints**:
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login and get access token
- `POST /api/v1/auth/refresh` - Refresh access token
- `GET /api/v1/users/me` - Get current user profile

**Technology**: Spring Security, JWT (JJWT), BCrypt, PostgreSQL

**Configuration** (`application.yml`):
```yaml
jwt:
  secret: ${JWT_SECRET:your-secret-key}
  access-token-expiration: 15m
  refresh-token-expiration: 7d
```

---

### 📦 Product Service (Port: 8081)

**Purpose**: Product catalog management with Redis caching

**Key Endpoints**:
- `POST /api/v1/products` - Create product
- `GET /api/v1/products/{id}` - Get product (cached)
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product
- `GET /api/v1/categories` - List categories

**Technology**: Spring Data JPA, Redis (Cache-Aside), Kafka Producer, PostgreSQL

**Caching Strategy**:
- Products cached in Redis with TTL of 30 minutes
- Cache invalidated on CREATE, UPDATE, DELETE operations
- Kafka events emitted for cache synchronization across services

**Events Published**:
- `ProductCreatedEvent` → Topic: `product-events`
- `ProductUpdatedEvent` → Topic: `product-events`
- `ProductDeletedEvent` → Topic: `product-events`

---

### 🔍 Search Service (Port: 8082)

**Purpose**: Full-text product search using Elasticsearch

**Key Endpoints**:
- `GET /api/v1/search/products?query=laptop` - Keyword search
- `GET /api/v1/search/products?category=electronics&priceFrom=100&priceTo=1000` - Faceted search
- `GET /api/v1/search/products?query=phone&sortBy=price&order=asc` - Sorted results

**Technology**: Spring Data Elasticsearch, Kafka Consumer, Elasticsearch

**Search Capabilities**:
- Fuzzy search with typo tolerance
- Multi-field search (name, description, category, tags)
- Price range filtering
- Category filtering
- Sorting by relevance, price, rating
- Pagination support

**Events Consumed**:
- `ProductCreatedEvent` → Index in Elasticsearch
- `ProductUpdatedEvent` → Update Elasticsearch index
- `ProductDeletedEvent` → Remove from index

---

### 📊 Inventory Service (Port: 8083)

**Purpose**: Stock management with concurrency control

**Key Endpoints**:
- `POST /api/v1/inventory/reserve` - Reserve stock for order
- `POST /api/v1/inventory/release` - Release reserved stock
- `GET /api/v1/inventory/{productId}` - Check stock levels

**Technology**: Spring Data JPA, Kafka Consumer/Producer, Optimistic Locking, PostgreSQL

**Concurrency Control**:
- **Optimistic Locking**: Version field prevents lost updates
- **Pessimistic Locking**: Optional for high-contention scenarios
- **Transaction Isolation**: Serializable isolation level for critical operations

**Stock Lifecycle**:
1. `AVAILABLE` → Available for purchase
2. `RESERVED` → Held pending payment
3. `CONFIRMED` → Sold and awaiting fulfillment
4. `RELEASED` → Returned to available on cancellation

**Events**:
- **Consumed**: `OrderCreatedEvent` → Reserve stock
- **Published**: `StockReservedEvent`, `StockReservationFailedEvent`

---

## 📨 API Documentation

### Authentication Flow

#### 1. Register User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

#### 2. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 900,
  "tokenType": "Bearer"
}
```

#### 3. Use Access Token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  http://localhost:8081/api/v1/products
```

### Product Management

#### Create Product

```bash
curl -X POST http://localhost:8081/api/v1/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro 15",
    "description": "High-performance laptop",
    "price": 1299.99,
    "categoryId": "cat-123",
    "stock": 50
  }'
```

#### Search Products

```bash
curl "http://localhost:8082/api/v1/search/products?query=laptop&category=electronics&sortBy=price&order=asc&page=1&size=20"
```

---

## 🔄 Event-Driven Architecture

### Event Topics & Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Kafka Topics                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  PRODUCT EVENTS           INVENTORY EVENTS       ORDER EVENTS       │
│  ────────────────         ────────────────       ──────────────     │
│  ProductCreated           StockReserved          OrderCreated       │
│  ProductUpdated           StockReleased          OrderCancelled     │
│  ProductDeleted           StockConfirmed         OrderConfirmed     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
    Search Service          Inventory Service        Order Service
    (Index in ES)           (Update Stock)          (Orchestrate Saga)
```

### Event Publishing Pattern

Services use **Transactional Outbox** to reliably publish events:

1. **Application Logic**: Save to database
2. **Outbox Entry**: Write event to `outbox` table in same transaction
3. **Event Relay**: Background job polls outbox and publishes to Kafka
4. **Cleanup**: Mark as published after successful Kafka send

### Event Consumption Pattern

Services consume events with **idempotent processing**:

1. **Kafka Consumer**: Listen on topic
2. **Deduplication**: Check if already processed (by event ID)
3. **Processing**: Update local state
4. **Error Handling**: Send to Dead Letter Topic (DLT) on failure
5. **Retry**: Automatic exponential backoff

---

## 👨‍💻 Development & Deployment

### Environment Configuration

Each service uses `application-{profile}.yml` for environment-specific configs:

```bash
# Development (default)
java -jar service.jar

# Production with Kubernetes
java -jar service.jar --spring.profiles.active=prod
```

### Database Initialization

PostgreSQL automatically initializes databases on first run:

```bash
# View init script
cat docker/postgres/init-databases.sql
```

The script creates:
- `user_db` - User service database
- `product_db` - Product service database
- `search_db` - Search service database (optional)
- `inventory_db` - Inventory service database
- `order_db` - Order service database
- `payment_db` - Payment service database
- `notification_db` - Notification service database

### Building & Running

#### Build All Services

```bash
./mvnw clean package
```

#### Build Specific Service

```bash
cd user-service
mvn clean package -DskipTests
java -jar target/user-service-1.0.0.jar
```

#### Build Docker Images

```bash
# Build user-service image
cd user-service
docker build -t ecommerce/user-service:latest .
docker run -p 8080:8080 ecommerce/user-service:latest
```

### Logging

Services use Spring Boot logging with JSON format for production:

```bash
# View logs
docker logs ecommerce-user-service

# Tail logs with timestamps
docker logs -f ecommerce-user-service | tail -100
```

### Health Checks

```bash
# Liveness probe (is service running?)
curl http://localhost:8080/actuator/health/liveness

# Readiness probe (is service ready to serve traffic?)
curl http://localhost:8080/actuator/health/readiness

# Detailed health info
curl http://localhost:8080/actuator/health
```

### Distributed Tracing

Access Zipkin UI to view traces:

```
http://localhost:9411
```

Search by:
- Service name
- Trace ID
- Tags (user ID, order ID, etc.)

---

## 🗓️ Project Roadmap

### ✅ Completed Phases

- **Phase 0**: Multi-module Maven structure & Docker Compose infrastructure
- **Phase 1**: User Service with JWT authentication
- **Phase 2**: Product Service with Redis caching & Kafka producers
- **Phase 3**: Kafka infrastructure with error handling (DLT, backoff)
- **Phase 4**: Elasticsearch Search Service with full-text capabilities
- **Phase 5**: Inventory Service with stock reservations & concurrency control

### 🔜 Planned Phases

- **Phase 6**: Order Service with Saga orchestration & Transactional Outbox
- **Phase 7**: Payment Service with mock payment gateway
- **Phase 8**: Notification Service with email delivery
- **Phase 9**: API Gateway with JWT authentication & rate limiting
- **Phase 10**: System integration, resilience patterns, end-to-end testing

### Future Enhancements

- [ ] Kubernetes deployment manifests
- [ ] Helm charts for production deployment
- [ ] Distributed transaction monitoring
- [ ] GraphQL API layer
- [ ] gRPC service-to-service communication
- [ ] Event sourcing implementation
- [ ] Comprehensive integration test suite
- [ ] Performance benchmarking suite
- [ ] API documentation with Swagger/OpenAPI
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Observability stack (Prometheus, Grafana, Loki)
- [ ] Multi-region deployment support

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Follow** the coding standards (see IMPLEMENTATION_PLAN.md)
4. **Test** your changes with existing test suite
5. **Commit** with clear messages (`git commit -m 'Add amazing feature'`)
6. **Push** to your branch (`git push origin feature/amazing-feature`)
7. **Open** a Pull Request with detailed description

### Coding Standards

- **Java 21 Features**: Use records, sealed classes, pattern matching
- **Constructor Injection**: No field injection with `@Autowired`
- **Immutability**: Prefer `record`s and `final` fields
- **No Business Logic in Controllers**: Keep controllers thin
- **Design Patterns**: Apply Saga, Outbox, Cache-Aside appropriately

### Code Review Checklist

- [ ] Code follows project standards
- [ ] Tests pass locally
- [ ] No hardcoded secrets or credentials
- [ ] Documentation updated
- [ ] No security vulnerabilities
- [ ] Performance impact assessed

---

## 📊 Architecture Decisions

### Why Microservices?

1. **Independent Scaling**: Scale services based on individual demand
2. **Technology Diversity**: Use best tool for each service
3. **Fault Isolation**: One service failure doesn't crash others
4. **Team Autonomy**: Teams can develop/deploy independently
5. **Business Alignment**: Services map to business domains

### Why Event-Driven?

1. **Loose Coupling**: Services don't call each other directly
2. **Eventual Consistency**: Accept short delays for consistency
3. **Audit Trail**: Complete event history for compliance
4. **Scalability**: Decouple message production from consumption
5. **Resilience**: Retry logic for failed message processing

### Why Database-per-Service?

1. **Data Isolation**: Each service owns its data
2. **Schema Evolution**: Services can change schemas independently
3. **Technology Choice**: Use different DB per service if needed
4. **Scaling**: Shard databases per service partition

### Trade-offs & Mitigation

| Challenge | Mitigation |
|-----------|-----------|
| Distributed Transactions | Saga pattern with compensating transactions |
| Data Consistency | Event-driven async + eventual consistency acceptance |
| Debugging Complexity | Distributed tracing (Zipkin), centralized logging |
| Network Latency | Service discovery, connection pooling, caching |
| Operational Overhead | Docker Compose, health checks, monitoring |

---

## 📚 References & Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Guide](https://kafka.apache.org/documentation/)
- [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Redis Documentation](https://redis.io/documentation)
- [Microservices Patterns](https://microservices.io/patterns/index.html)
- [Event Sourcing Pattern](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)
- [IMPLEMENTATION_PLAN.md](./IMPLEMENTATION_PLAN.md) - Detailed design document
- [PROGRESS.md](./PROGRESS.md) - Phase-by-phase progress tracking

---

## 📞 Support & Questions

For issues, questions, or suggestions:

1. **GitHub Issues**: [Open an issue](https://github.com/surendar029/E-Commerce/issues)
2. **Discussions**: [Start a discussion](https://github.com/surendar029/E-Commerce/discussions)
3. **Documentation**: See [IMPLEMENTATION_PLAN.md](./IMPLEMENTATION_PLAN.md) for technical details

---

## 📄 License

This project is licensed under the **MIT License** - see LICENSE file for details.

---

## 👤 Author

**Surendar029**

- GitHub: [@surendar029](https://github.com/surendar029)
- Email: Contact via GitHub

---

## ⭐ Show Your Support

If you find this project helpful, please consider giving it a star ⭐ on GitHub!

---

## 🎯 Glossary

| Term | Definition |
|------|-----------|
| **EDA** | Event-Driven Architecture |
| **JWT** | JSON Web Token for stateless authentication |
| **Saga** | Distributed transaction pattern for multi-service workflows |
| **DLT** | Dead Letter Topic for failed message handling |
| **Idempotent** | Safe to execute multiple times without side effects |
| **Eventually Consistent** | System reaches consistency over time, not immediately |
| **Circuit Breaker** | Pattern to prevent cascading failures |
| **Outbox** | Temporary storage for events before publishing to message broker |
| **Offset** | Position in Kafka topic partition for consumer tracking |
| **Shard** | Horizontal partition of data across instances |

---

**Last Updated**: January 2025  
**Status**: Phase 5 Complete - Inventory Service Ready  
**Next Phase**: Order Service & Saga Orchestration
