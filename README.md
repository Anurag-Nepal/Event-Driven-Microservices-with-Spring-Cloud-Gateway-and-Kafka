# Microservices Ecosystem with Spring Cloud, Keycloak, and Kafka

This project demonstrates a production‑ready microservices architecture built with Spring Boot, Spring Cloud, Netflix Eureka, Spring Cloud Gateway, Keycloak (OAuth2 / JWT), and Apache Kafka. It implements a secure, resilient, and scalable asynchronous job processing pipeline.

## Architecture Overview

The system is composed of several independent services that communicate via REST and a message broker. All services are registered with a central service registry (Eureka) and are fronted by an API Gateway, which acts as the single entry point for external clients.

text

```
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   Client    │ ──→  │ API Gateway  │ ──→  │ Auth Service │ ──→ Keycloak
└─────────────┘      │   (port 8080)│      └──────────────┘
                     └──────────────┘
                            │
                            ↓
                     ┌──────────────┐      ┌──────────────┐
                     │    Eureka    │      │    Redis     │
                     │  Discovery   │      │ (Rate Limit) │
                     └──────────────┘      └──────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌──────────────────┐         ┌────────────────────────┐   │
│  │ Producer Service │──Kafka─→│ Consumer Service       │   │
│  │  (job ingestion) │         │ (async job processing) │   │
│  └──────────────────┘         └────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```



- **Eureka Discovery Server** – service registry (port 8761).
- **API Gateway** – single entry point (port 8080). Validates JWT tokens, applies rate limiting, circuit breakers, and routes requests.
- **Auth Service** – manages user registration and login, issues JWT tokens via Keycloak (port 8083).
- **Producer Service** – exposes REST endpoints to submit async jobs (e.g., email, PDF generation). Publishes job messages to Kafka (port 8081).
- **Consumer Service** – listens to Kafka topics and processes jobs asynchronously (simulated with logging and delay) (port 8082).
- **Keycloak** – identity provider (port 8900) with realm `myrealm` and clients `api-gateway` (for login) and `admin-cli` (for user registration).
- **Kafka** – message broker (port 9092) in KRaft mode; topic `async-jobs`.
- **Redis** – used by the gateway for distributed rate limiting (port 6379).
- **MySQL** – optional database for persisting job status (port 3306).

## Core Interactions

### 1. Authentication & Authorization

- **Registration**
  `POST /auth/register` (via gateway) – auth service calls Keycloak Admin API (using `admin-cli` client credentials) to create a new user.
- **Login**
  `POST /auth/login` (via gateway) – auth service forwards credentials to Keycloak’s token endpoint (using `api-gateway` client) and returns an access token and refresh token.
- **Secured API Calls**
  For protected routes (`/producer/**`, `/consumer/**`), the client includes `Authorization: Bearer <token>`.
  The gateway validates the token locally (using cached public keys from Keycloak) and, if valid, forwards the request to the appropriate service. **No further authentication is performed by the downstream services** – they trust the gateway.

### 2. Asynchronous Job Processing

- **Job Submission**
  `POST /producer/api/jobs` (JWT required) – the producer service accepts a `JobRequest` (containing `type` and `payload`), assigns a unique `jobId`, and immediately publishes it to Kafka topic `async-jobs`. It responds with a success message containing the job ID.

- **Job Processing**
  The consumer service listens to the same Kafka topic. On receiving a message, it:

  - Logs the full job details in JSON format.
  - Simulates processing with a `Thread.sleep(5000)` (representing email sending, PDF generation, etc.).
  - Logs completion.

  This decouples job submission from execution, ensuring that the producer remains fast and resilient even under heavy load.

## Resilience and Security Features

### API Gateway

- **JWT Validation** – OAuth2 resource server configured with Keycloak’s issuer URI; validates tokens on every request.
- **Rate Limiting** – Redis-based `RequestRateLimiter` (IP‑keyed) with configurable replenish and burst rates.
- **Circuit Breaker** – Resilience4J circuit breakers with fallback responses for each route.
- **Retry** – Automatic retries on transient errors (e.g., 503, 504) with exponential backoff.
- **Timeouts** – Connection and response timeouts prevent hung requests.
- **CORS** – Global CORS configuration (if a frontend is used).
- **Actuator** – Health and metrics endpoints for monitoring.

### Services

- **Service Discovery** – All services register with Eureka, enabling dynamic load balancing and resilience.
- **Database Integration** – Producer and consumer can optionally store job status in MySQL (JPA configured but not used in the demo).
- **Idempotency** – Not implemented in this demo, but would be added via a processed‑jobs store for production use.

## Technology Stack

- **Java 17** + **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.3** (Gateway, Netflix Eureka, Circuit Breaker, LoadBalancer)
- **Spring Security** + **OAuth2 Resource Server** (JWT validation)
- **Spring Kafka** – reactive Kafka producer/consumer
- **Keycloak** – identity provider (OIDC)
- **Redis** – rate limiting store
- **MySQL** – optional persistence
- **Maven** – build tool
- **Docker** – containerisation of infrastructure (Kafka, Redis, Keycloak, MySQL)

## Summary

This project demonstrates a modern microservices architecture where:

- **Security** is centralised via Keycloak and enforced at the gateway.
- **Resilience** is built in with circuit breakers, retries, timeouts, and rate limiting.
- **Asynchronous processing** is achieved through Kafka, decoupling request handling from heavy background tasks.
- **Service discovery** enables dynamic routing and load balancing.

The result is a scalable, maintainable, and production‑ready foundation for any asynchronous job processing system.
