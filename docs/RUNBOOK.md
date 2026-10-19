# Operational Runbook: AI Blood Donor Network Backend

This runbook describes operational procedures, incident response workflows, and maintenance tasks for the backend service.

---

## 1. Architecture & Service Overview

- **Service:** Spring Boot 4.1.1 (Java 21) on Apache Tomcat
- **Primary Database:** PostgreSQL 16 on Neon Serverless (SSL required)
- **Cache & Rate Limiting:** Redis 7 (Lettuce client)
- **Microservice Integration:** AI Donor Matching Service (HTTP / FastAPI)
- **Deployment Platform:** Render (Web Service, Docker container, 512MB RAM target)

---

## 2. Health Probes & Cold Starts

### Probes
- **Liveness Probe:** `/actuator/health/liveness`
  - Validates `diskSpace` and `ping`.
  - **Does NOT** query database or Redis.
  - Safe for Render health checks (avoids keeping serverless PostgreSQL awake).
- **Readiness Probe:** `/actuator/health/readiness`
  - Validates `db` and `redis`.
  - Used for traffic routing readiness.
- **Info Endpoint:** `/actuator/info`
  - Returns git commit SHA, build timestamp, branch name, and version info.

### Cold-Start Behavior
When deployed on free tiers, inactive instances spin down. When traffic arrives:
1. Container starts up (~15–25 seconds for JVM & Spring Boot initialization).
2. Neon Serverless Postgres wakes up from suspended state (~3–5 seconds).
3. If user requests take >5s, the frontend displays:
   `"Server shuru ho raha hai (free hosting), kripya 30-50s intezaar karein..."`
4. Idempotent GET requests automatically retry up to 3 times (2s, 4s, 8s backoff).

---

## 3. Incident Response Playbooks

### A. Database Connection Failure (500 / 503)
**Symptoms:** Logs show `HikariPool-1 - Connection is not available, request timed out after 30000ms`.
1. Verify Neon PostgreSQL status in Neon Console.
2. Check if connection string includes `sslmode=require` and pooled endpoint `prepareThreshold=0`.
3. Verify role credentials (`app_user`).
4. Ensure `DB_POOL_MAX` is within tier limit (default 10).

### B. Redis Unavailable (Fail-Closed / Local Memory Fallback)
**Symptoms:** Logs show `Redis rate limiting failed, falling back to local memory rate limiting`.
1. Rate limiting gracefully falls back to local memory bucket algorithms to prevent outage.
2. Check Redis connectivity: `redis-cli -h <host> -p <port> ping`.
3. If memory pressure on Redis, check key eviction policy (`volatile-lru` or `allkeys-lru`).

### C. Out of Memory (OOM) Kill
**Symptoms:** Container restarts with exit code 137.
1. JVM flags in `Dockerfile` set `-XX:MaxRAMPercentage=75.0` and `-XX:+ExitOnOutOfMemoryError`.
2. Ensure Tomcat threads max is tuned to `50` (in `application-prod.properties`).
3. Ensure Hikari pool maximum is set to `10` or lower.

---

## 4. Database Backup & Restore

### Automated / Scheduled Backups
Use `scripts/backup_neon.sh` with `DATABASE_URL` environment variable:
```bash
export DATABASE_URL="postgresql://neondb_owner:***@ep-***.neon.tech/blood_donor_db?sslmode=require"
./scripts/backup_neon.sh
```

### Restore Procedure
```bash
pg_restore --clean --if-exists --no-owner --no-privileges -d "$DATABASE_URL" backup_neon_TIMESTAMP.dump
```

---

## 5. Rollback Procedure
If a deployment fails:
1. Revert Render deployment to the previous successful deploy in the Render Dashboard.
2. In git: checkout previous stable tag/commit on `release/final` and deploy.
3. If Flyway migration rollback is required, run compensating migration script `V<next>__rollback_...`.
