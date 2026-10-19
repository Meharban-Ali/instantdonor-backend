# 🩸 AI Blood Donor Network — Backend Service

> High-throughput, production-hardened emergency blood donor network backend built with Java 21, Spring Boot 4.1.1, PostgreSQL 16 (Neon Serverless), Redis 7, and Docker.

---

## 🏗️ Architecture

```
                                  ┌───────────────────────────┐
                                  │      Client Applications   │
                                  │  (Web / React, Mobile App)│
                                  └─────────────┬─────────────┘
                                                │ (HTTPS / JWT)
                                                ▼
                                  ┌───────────────────────────┐
                                  │   Spring Boot 4.1.1 API   │
                                  │   (TraceIdFilter + MDC)   │
                                  │   (RateLimitingFilter)    │
                                  │   (JwtAuthFilter)         │
                                  └──────┬──────┬──────┬──────┘
                                         │      │      │
            ┌────────────────────────────┘      │      └────────────────────────────┐
            ▼                                   ▼                                   ▼
┌───────────────────────┐           ┌───────────────────────┐           ┌───────────────────────┐
│  PostgreSQL 16 (Neon) │           │     Redis 7 Cache     │           │   AI Python Service   │
│  (Flyway Migrations)  │           │   (Token Blacklist,   │           │   (FastAPI Scoring,   │
│  (Optimistic Locks)   │           │    Rate Limits, TTL)  │           │    Rule-based MCDA)   │
└───────────────────────┘           └───────────────────────┘           └───────────────────────┘
```

---

## 🛠️ Tech Stack & Versions

- **Java:** OpenJDK 21 LTS
- **Framework:** Spring Boot 4.1.1 (Spring Framework 7.0.9)
- **Database:** PostgreSQL 16 on Neon Serverless (SSL enforced, `prepareThreshold=0` for pooled endpoints)
- **Database Migration:** Flyway 12.4.0 (V1 Init, V2 Indexes, V3 Notification Consent)
- **Cache & Rate Limiting:** Redis 7 (Lettuce client)
- **Security:** Spring Security 7, JJWT 0.12.5 (HMAC-SHA384)
- **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers 1.20 (PostgreSQL 16, Redis 7)

---

## ⚙️ Environment Variables Reference Table

| Variable | Required in Prod | Default | Purpose / Description |
|:---|:---:|:---|:---|
| `SPRING_PROFILES_ACTIVE` | Yes | `dev` | Active Spring profile (`prod`, `dev`, `integration`). |
| `SERVER_PORT` | No | `8080` | HTTP port on which Tomcat listens. |
| `DB_URL` | **YES** | *None* | JDBC connection URL for PostgreSQL (e.g. `jdbc:postgresql://ep-xyz.neon.tech/blood_donor_db?sslmode=require`). |
| `DB_USERNAME` | **YES** | *None* | PostgreSQL database username (`app_user`). |
| `DB_PASSWORD` | **YES** | *None* | PostgreSQL database password (fail-fast enforced on startup). |
| `DB_POOL_MAX` | No | `10` | Maximum Hikari connection pool size (tuned for 512MB RAM and cloud limits). |
| `DB_PREPARE_THRESHOLD`| No | `0` | JDBC driver server-side prepare threshold (`0` disables named prepared statements for PgBouncer). |
| `REDIS_HOST` | **YES** | *None* | Hostname or IP of the Redis server. |
| `REDIS_PORT` | **YES** | `6379` | Port for the Redis server. |
| `REDIS_PASSWORD` | Optional | *Empty* | Authentication password for Redis (if enabled). |
| `JWT_SECRET` | **YES** | *None* | Secret key for JJWT HMAC-SHA384 (strictly minimum 32 bytes / 256 bits). |
| `JWT_EXPIRATION` | No | `86400000` | Access token lifespan in milliseconds (24 hours). |
| `JWT_REFRESH_EXPIRATION` | No | `604800000` | Refresh token lifespan in milliseconds (7 days). |
| `ADMIN_NAME` | No | `Super Admin` | Full name of the bootstrapped system administrator. |
| `ADMIN_EMAIL` | **YES (if ADMIN_PASSWORD set)** | *None* | Email address of administrator (validated on prod startup). |
| `ADMIN_PASSWORD` | Optional | *None* | Password for administrator bootstrap (skips admin seeding if unset). |
| `AI_SERVICE_URL` | No | `http://ai-service:8000` | Base URL of the Python AI matching microservice. |
| `AI_SERVICE_API_KEY` | **YES** | *None* | Shared secret API key for AI microservice (minimum 32 characters in prod). |
| `FRONTEND_URL` | Yes | `http://localhost:5173` | Client origin URL for reset-password links and CORS. |
| `CORS_ALLOWED_ORIGINS`| No | `http://localhost:5173` | Comma-separated list of permitted CORS origins. |
| `TWILIO_ACCOUNT_SID` | Optional | *None* | Twilio Account SID for emergency SMS alerts. |
| `TWILIO_AUTH_TOKEN` | Optional | *None* | Twilio Auth Token. |
| `TWILIO_PHONE_NUMBER` | Optional | *None* | Twilio Sender Phone Number. |

---

## 🚀 Local Development & Build Commands

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Docker Desktop (for integration tests & Testcontainers)

### Run Unit & Integration Tests
```bash
# Run unit and integration tests (Testcontainers starts Postgres 16 & Redis 7 automatically)
mvn clean test
```

### Build Production Artifact
```bash
# Builds fat JAR with build-info.properties and git.properties
mvn clean package -DskipTests
```

### Health Probes
- **Liveness:** `GET /actuator/health/liveness` (lightweight, zero DB/Redis queries, safe for cloud health checks)
- **Readiness:** `GET /actuator/health/readiness` (deep check on DB and Redis)
- **Info:** `GET /actuator/info` (commit SHA, branch, and version metadata)