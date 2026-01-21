# Spring Boot Microservices Demo

A microservices demo application built with **Spring Boot**, showcasing a simple distributed architecture with:

- **API Gateway**
- **Service Discovery**
- **Inventory Service**
- **Order Service**
- **Product Service**

This was created as a **side project during my internship** to practice designing, implementing, and running microservices with Spring.

---

## Architecture Overview

The system is organized as a **multi-module Maven/Gradle project** with a parent module called **`microservice`** and several child services:

- **api-gateway**
  - Routes external requests to internal services
  - Central entry point to the system
  - Handles path-based routing (and can be extended with auth, rate limiting, etc.)

- **discovery-service**
  - Service registry (e.g. using Eureka)
  - All services register themselves and discover each other using logical names

- **inventory-service**
  - Manages product inventory and stock levels
  - Example endpoints: check stock, update quantities

- **order-service**
  - Handles order creation and retrieval
  - Communicates with `inventory-service` (and `product-service`) to validate stock and product data

- **product-service**
  - Manages product catalog (basic product info, pricing, etc.)
  - Provides product details to other services

All services are independent Spring Boot applications and can be started separately.

---

## Technologies Used

- **Language:** Java
- **Framework:** Spring Boot
- **Microservices:**
  - Spring Cloud (API Gateway, Service Discovery)
- **Build Tool:** Maven
- **Configuration:** application YAML/Properties
- **Others (optional, depending on your project):**
  - Spring Web
  - Spring Data JPA
  - H2 / MySQL / PostgreSQL (or in-memory storage)
  - OpenFeign / RestTemplate for inter-service communication

---

## Getting Started

### Prerequisites

- Java 17+ (or the version you used)
- Maven or Gradle installed
- Git

### Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
