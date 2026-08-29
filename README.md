# E-Commerce Microservices Platform

A scalable, event-driven e-commerce backend platform built using **Java 21** and **Spring Boot**. The architecture uses microservices communicating asynchronously via **Apache Kafka**, backed by **Redis** for high-performance caching and **Elasticsearch** for rapid product searching.

---

## Architecture & Technology Stack

* **Core Framework:** Spring Boot, Spring Cloud
* **Language:** Java 21
* **Messaging & Event Streaming:** Apache Kafka
* **Caching & State Management:** Redis
* **Search Engine:** Elasticsearch
* **Database:** PostgreSQL / MySQL (per service)
* **Containerization:** Docker & Docker Compose

---

## Key Features

* **Event-Driven Architecture:** Decoupled services communicating via Kafka topics to ensure eventual consistency and high fault tolerance (e.g., Order creation triggering inventory updates and notifications asynchronously).
* **Distributed Caching:** Redis implementation for fast shopping cart management, session handling, and catalog caching.
* **Full-Text Product Search:** Elasticsearch integration for blazing-fast filtering, searching, and catalog discovery.
* **Resilient Microservices:** Modular design ensuring isolated domain boundaries for Orders, Inventory, Products, and Users.
* **Container-Ready:** Fully containerized setup via Docker Compose for easy local testing and deployment.

---

## Project Structure

```text
E-Commerce/
│
├── api-gateway/           # Routes requests and handles edge security
├── discovery-service/     # Service registration and discovery (Eureka)
├── order-service/         # Manages order lifecycles and publishes Kafka events
├── inventory-service/     # Handles stock levels and listens to order events
├── product-service/       # Manages product catalogs and syncs with Elasticsearch
├── notification-service/  # Consumes asynchronous alerts/events for users
└── docker-compose.yml     # Multi-container orchestration for local environment
