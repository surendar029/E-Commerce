# E-Commerce Microservices

## Description

A **production-grade, event-driven E-Commerce microservices platform** built with **Java 21 and Spring Boot 3**, designed for scalability, resilience, and asynchronous communication across distributed services.
The system separates core business capabilities into independent services for **user authentication, product catalog, inventory management, and product search**, with **Apache Kafka** enabling event-driven communication between services. **Redis** provides low-latency caching, while **Elasticsearch** powers fast full-text product discovery and filtering.

The architecture incorporates modern distributed-system patterns including **database-per-service, Saga-based transaction workflows, Transactional Outbox, Cache-Aside, Circuit Breaker, and distributed tracing**, making the project a practical implementation of scalable backend and microservices architecture.

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
## Category 
- Microservices & Distributed Systems
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
