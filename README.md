# 🩸 AI Blood Donor Network — Production Backend

> High-throughput, production-ready AI-powered emergency blood donor network backend built with Java 21, Spring Boot 3.2.5, MySQL 8, Redis 7, and Docker.

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
                                  │   Spring Boot 3.2 API     │
                                  │   (TraceIdFilter + MDC)   │
                                  │   (RateLimitingFilter)    │
                                  │   (JwtAuthFilter)         │
                                  └──────┬──────┬──────┬──────┘
                                         │      │      │
            ┌────────────────────────────┘      │      └────────────────────────────┐
            ▼                                   ▼                                   ▼
┌───────────────────────┐           ┌───────────────────────┐           ┌───────────────────────┐
│   MySQL 8.0 Cluster   │           │     Redis 7 Cache     │           │   AI Python Service   │
│   (Flyway Migrations) │           │   (Token Blacklist,   │           │   (FastAPI Scoring,   │
│   (Optimistic Locks)  │           │    Rate Limits, TTL)  │           │    City Fallback)     │
└───────────────────────┘           └───────────────────────┘           └───────────────────────┘
```

---

## ⚙️ Environment Variables Reference Table

| Variable | Required in Prod | Default | Purpose / Description |
|:---|:---:|:---|:---|
| `SPRING_PROFILES_ACTIVE` | Yes | `dev` | Active Spring profile (`prod`, `dev`, `integration`). |
| `SERVER_PORT` | No | `8080` | HTTP port on which Tomcat listens. |
| `DB_URL` | **YES** | *None* | JDBC connection URL for MySQL (e.g. `jdbc:mysql://mysql:3306/blood_donor_db`). |
| `DB_USERNAME` | **YES** | *None* | MySQL database username. |
| `DB_PASSWORD` | **YES** | *None* | MySQL database password (fail-fast enforced on startup). |
| `REDIS_HOST` | **YES** | *None* | Hostname or IP of the Redis server. |
| `REDIS_PORT` | **YES** | `6379` | Port for the Redis server. |
| `REDIS_PASSWORD` | Optional | *Empty* | Authentication password for Redis (if enabled). |
| `JWT_SECRET` | **YES** | *None* | Secret key for JJWT HMAC-SHA384 (strictly minimum 48 bytes / 384 bits). |
| `JWT_EXPIRATION` | No | `86400000` | Access token lifespan in milliseconds (24 hours). |
| `JWT_REFRESH_EXPIRATION` | No | `604800000` | Refresh token lifespan in milliseconds (7 days). |
| `ADMIN_NAME` | No | `Super Admin` | Full name of the bootstrapped system administrator. |
| `ADMIN_EMAIL` | No | `admin@blooddonor.org` | Email address of the system administrator. |
| `ADMIN_PASSWORD` | **YES** | *None* | Password for default administrator (hardcoded default removed; required for bootstrap). |
| `AI_SERVICE_URL` | No | `http://ai-service:8000` | Base URL of the Python AI matching microservice. |
| `FRONTEND_URL` | Yes | `http://localhost:3000` | Client origin URL for reset-password links and CORS. |
| `CORS_ALLOWED_ORIGINS`| No | `http://localhost:3000` | Comma-separated list of permitted CORS origins. |
| `MAIL_HOST` | Optional | `smtp.gmail.com` | SMTP relay server host. |
| `MAIL_PORT` | Optional | `587` | SMTP relay port (TLS). |
| `MAIL_USERNAME` | Optional | *None* | SMTP username / email address. |
| `MAIL_PASSWORD` | Optional | *None* | SMTP application-specific password. |
| `TWILIO_ACCOUNT_SID` | Optional | *None* | Twilio Account SID for emergency SMS alerts. |
| `TWILIO_AUTH_TOKEN` | Optional | *None* | Twilio Auth Token for emergency SMS alerts. |
| `TWILIO_PHONE_NUMBER` | Optional | *None* | Twilio originating phone number (E.164 format). |

---

## 🚀 Running & Testing Commands

### 1. Build and Run Locally
```bash
# Compile and start with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Start in production mode with required environment variables
export DB_URL="jdbc:mysql://localhost:3306/blood_donor_db"
export DB_USERNAME="root"
export DB_PASSWORD="YourSecurePassword123!"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export JWT_SECRET="ThisIsAVerySecureSecretKeyWithAtLeastFortyEightBytesForSha384!"
export ADMIN_PASSWORD="Admin@StrongPassword123!"
java -jar target/ai-blood-donor-network-1.0.0.jar --spring.profiles.active=prod
```

### 2. Run Comprehensive Test Suite (Unit + Testcontainers MySQL 8 & Redis)
```bash
# Run all tests (real MySQL 8 and Redis automatically spawned via Testcontainers)
mvn clean test
```

### 3. Run OWASP Dependency-Check Scan
```bash
# Scan for known CVE vulnerabilities in dependencies
mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=8
```

### 4. Run Concurrency & Load Benchmark
```bash
# Execute 100 concurrent workers test against real database
mvn test -Dtest=RealDatabasePhase5LoadTest

# Or run k6 distributed load test script
k6 run scripts/load_test_k6.js
```

### 5. Run with Docker Compose
```bash
# Spin up MySQL, Redis, Spring Boot Backend, and optional AI service
docker compose up -d --build
```

---

## 🛡️ Reliability & High-Availability Runbook

### 1. What happens when Redis is DOWN?
* **Token Blacklist**: Enforces a strict **fail-closed** security policy. Requests bearing tokens return HTTP `503 Service Unavailable`, preventing revoked or compromised tokens from executing operations while the blacklist is unreachable.
* **Rate Limiting**: Gracefully falls back to local in-memory sliding window counters so legitimate requests continue to be processed.
* **Cache Eviction**: Falls back to direct database reads; donor searches query MySQL directly using optimized compound indexes.

### 2. What happens when the AI Service is DOWN?
* **Matching Fallback**: If `AI_SERVICE_URL` returns connection refused, timeout, or HTTP 5xx, `AiDonorMatchingStrategy` automatically falls back to the database-driven SQL city matching query.
* **Zero Disruption**: Blood requests are created and dispatched to donors without interruption.
* **Observability**: Increments the custom Micrometer counter `ai.matching.fallback` for live alerting.

### 3. What happens when MySQL Database is DOWN?
* **Connection Pool**: HikariCP maintains retry backoff up to `connection-timeout` (30 seconds).
* **Graceful Degradation**: Endpoints return a standardized `ApiResponse` with HTTP `503 Service Unavailable`.
* **Probes**: Actuator readiness probe `/actuator/health/readiness` reports `DOWN`, prompting load balancers to route traffic away from the container.

---

## 🔒 Security & Data Protection (DPDP)

* **Uniform Error Responses**: All status codes (400, 401, 403, 404, 409, 429, 500, 503) return standard `ApiResponse<T>` JSON containing both `message` and `error` fields.
* **Security Headers**: HSTS, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: strict-origin-when-cross-origin`.
* **PII Redaction**: Phone numbers and emails are masked in all audit logs, trace MDC logs, and external API previews.
* **Right to Erasure (DPDP)**: `DELETE /api/auth/me` anonymizes user records, scrambles credentials, and cancels pending requests.
* **Account Lockout**: 5 consecutive failed login attempts trigger an automatic 15-minute lockout (HTTP 429).