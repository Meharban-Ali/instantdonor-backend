# Cloud Deployment Guide: Blood Donor Network
**Target Stack**: Vercel (Frontend) + Render (Spring Boot Backend & FastAPI AI Service) + External MySQL 8 (TLS) + Managed Redis (TLS)

---

## 1. Architecture Overview

```
                      ┌──────────────────────────────────────────┐
                      │          Browser / Mobile Web            │
                      └─────────────────────┬────────────────────┘
                                            │
                                            ▼
                      ┌──────────────────────────────────────────┐
                      │              Vercel Edge                 │
                      │       https://instantdonor.online        │
                      │       (Vite React SPA + vercel.json)     │
                      └─────────────────────┬────────────────────┘
                                            │ API Requests (CORS / Bearer)
                                            ▼
                      ┌──────────────────────────────────────────┐
                      │             Render Web Service           │
                      │     https://api.instantdonor.online      │
                      │     (Spring Boot 3 / Java 21 Docker)     │
                      └───────┬──────────────────────────┬───────┘
                              │                          │
        Internal HTTP + X-API-KEY                        │ JDBC TLS
                              ▼                          ▼
               ┌────────────────────────┐      ┌────────────────────────┐
               │   Render Web Service   │      │    External MySQL 8    │
               │   FastAPI AI Service   │      │    (Aiven / TiDB)      │
               │   (Python 3.11 Docker) │      │   TLS / Flyway Managed │
               └──────────────┬─────────┘      └────────────────────────┘
                              │                          ▲
                              │ Lettuce SSL              │ PyMySQL TLS
                              ▼                          │
               ┌────────────────────────┐                │
               │   Render Key Value /   │                │
               │      Upstash Redis     │────────────────┘
               │  (Token Blacklist/TTL) │
               └────────────────────────┘
```

---

## 2. Infrastructure Requirements & Pre-Deployment Checklist

### A. External MySQL 8 Database
- **Recommended Free Providers**: Aiven for MySQL (free plan) or TiDB Cloud Serverless.
- **Connection Requirements**:
  - Must support TLSv1.2 or TLSv1.3.
  - Connection URL Format:
    ```
    jdbc:mysql://<host>:<port>/<dbname>?useSSL=true&requireSSL=true&verifyServerCertificate=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
    ```
  - **Hikari Connection Pool Constraints**:
    Free database tiers strictly limit concurrent connections (e.g., 20 max). Configure Backend with:
    `DB_POOL_MAX=10` and `DB_POOL_MIN_IDLE=2`.
  - **Schema Migration**:
    Flyway automatically executes all migrations (`V1`, `V2`, `V3`) on startup. Ensure the database user has `CREATE TABLE`, `ALTER TABLE`, `INDEX`, `INSERT`, `UPDATE`, `DELETE`, and `SELECT` privileges.

### B. Redis Instance
- **Recommended Providers**: Render Key Value, Upstash Redis, or Aiven for Redis.
- **Connection Requirements**:
  - TLS/SSL enabled: `REDIS_SSL=true` or connection URI starting with `rediss://`.
  - Used for rate-limiting, authentication lockout, and JWT token revocation blacklist.
  - *Note on Resilience*: `JwtAuthFilter` enforces a fail-closed policy for token blacklisting with a 3000ms timeout. If Redis is unreachable, authenticated requests fail with HTTP 503.

### C. Email Service (Resend SMTP)
- **Render Free Tier Restriction**: Render blocks outbound connections on standard SMTP ports `25`, `465`, and `587` on free web services to prevent spam.
- **Solution**: Resend officially supports non-standard submission port `2587` (with STARTTLS) and port `2465` (with implicit TLS).
- **Configuration**:
  - `MAIL_HOST=smtp.resend.com`
  - `MAIL_PORT=2587`
  - `MAIL_USERNAME=resend`
  - `MAIL_PASSWORD=re_your_resend_api_key`
  - `MAIL_FROM=noreply@instantdonor.online`

---

## 3. Step-by-Step Deployment Procedure

### Step 1: Deploy AI Service on Render
1. In the Render Dashboard, select **New +** > **Web Service**.
2. Connect your Git repository (`ai-service` repository).
3. Select **Docker** environment and **Free** instance type.
4. Set **Health Check Path** to `/health`.
5. Configure Environment Variables (see Section 4 Table).
6. Note the deployed public URL (e.g. `https://blood-donor-ai-service.onrender.com`).

### Step 2: Deploy Spring Boot Backend on Render
1. In the Render Dashboard, select **New +** > **Web Service**.
2. Connect your Git repository (`Backend` repository).
3. Select **Docker** environment and **Free** instance type (512MB RAM).
4. **Health Check Path**: `/actuator/health`.
5. **JVM Memory Optimization Flags** (already baked into `Dockerfile`):
   ```
   JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=55.0 -XX:InitialRAMPercentage=40.0 -Xss512k -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError"
   ```
6. Add custom domain `api.instantdonor.online` in Render service settings and add the CNAME record in your DNS registrar pointing to your Render service address.
7. Configure Environment Variables (see Section 4 Table).

### Step 3: Deploy Frontend on Vercel
1. In Vercel, select **Add New Project** and import the `Frontend` repository.
2. Select **Vite** Framework Preset.
3. Configure Build Settings:
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Set Environment Variable:
   - `VITE_API_BASE_URL`: `https://api.instantdonor.online/api`
5. Deploy. Vercel automatically applies [`vercel.json`](file:///vercel.json) rules:
   - Single Page Application (SPA) catch-all rewrite to `/index.html`.
   - Security headers: `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Referrer-Policy: strict-origin-when-cross-origin`.
   - Content Security Policy: `connect-src 'self' https://api.instantdonor.online`.
   - Private indexing protection: `X-Robots-Tag: noindex, nofollow`.
6. Attach Custom Domain `instantdonor.online` and `www.instantdonor.online` in Vercel settings and configure DNS A/CNAME records.

---

## 4. Environment Variables Reference Tables

### Service 1: Spring Boot Backend (`api.instantdonor.online`)

| Variable | Required | Source / Provider | Description / Example |
| :--- | :---: | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Yes | App Config | Must be `prod` |
| `PORT` | Auto | Render | Automatically assigned by Render (e.g. `10000`) |
| `DB_URL` | Yes | MySQL Provider | `jdbc:mysql://host:port/dbname?useSSL=true&requireSSL=true&verifyServerCertificate=true` |
| `DB_USERNAME` | Yes | MySQL Provider | Database user name |
| `DB_PASSWORD` | Yes | MySQL Provider | Database user password |
| `DB_POOL_MAX` | Optional | Performance Tuning | Max Hikari connections (default `10`) |
| `DB_POOL_MIN_IDLE` | Optional | Performance Tuning | Min idle connections (default `2`) |
| `REDIS_HOST` | Yes* | Redis Provider | Redis hostname (e.g. `red-xxxx.render.com`) |
| `REDIS_PORT` | Yes* | Redis Provider | Redis port (`6379`) |
| `REDIS_PASSWORD` | Optional | Redis Provider | Redis password |
| `REDIS_SSL` | Yes | Redis Provider | `true` for cloud providers requiring TLS |
| `REDIS_URL` | Optional | Redis Provider | Full URI format (`rediss://default:pass@host:port`) |
| `JWT_SECRET` | Yes | User Generated | Cryptographically secure string, min 48 chars (`openssl rand -hex 32`) |
| `JWT_EXPIRATION` | Optional | App Config | Access token validity in ms (`86400000` = 24h) |
| `JWT_REFRESH_EXPIRATION` | Optional | App Config | Refresh token validity in ms (`604800000` = 7d) |
| `FRONTEND_URL` | Yes | App Config | `https://instantdonor.online` |
| `CORS_ALLOWED_ORIGINS` | Yes | App Config | `https://instantdonor.online,https://www.instantdonor.online` |
| `AI_SERVICE_URL` | Yes | Render AI URL | `https://blood-donor-ai-service.onrender.com` |
| `AI_SERVICE_API_KEY` | Yes | User Generated | Shared secret matching `API_KEY` in AI service |
| `MAIL_HOST` | Optional | Resend | `smtp.resend.com` |
| `MAIL_PORT` | Optional | Resend | `2587` (bypasses Render free SMTP block) |
| `MAIL_USERNAME` | Optional | Resend | `resend` |
| `MAIL_PASSWORD` | Optional | Resend | Resend API Key (`re_...`) |
| `MAIL_FROM` | Optional | Resend | `noreply@instantdonor.online` |
| `MAIL_REPLY_TO` | Optional | App Config | `support@instantdonor.online` |
| `TWILIO_ACCOUNT_SID` | Optional | Twilio | Twilio Account SID |
| `TWILIO_AUTH_TOKEN` | Optional | Twilio | Twilio Auth Token |
| `TWILIO_PHONE_NUMBER` | Optional | Twilio | Twilio Phone Number |
| `TWILIO_MESSAGING_SERVICE_SID`| Optional | Twilio | Messaging Service SID |

### Service 2: FastAPI AI Service (`ai-service`)

| Variable | Required | Source / Provider | Description / Example |
| :--- | :---: | :--- | :--- |
| `ENVIRONMENT` | Yes | App Config | Must be `production` |
| `DEBUG` | Yes | App Config | Must be `false` (enforced at startup) |
| `PORT` | Auto | Render | Automatically assigned by Render |
| `API_KEY` | Yes | User Generated | Secret key for `X-API-KEY` verification (matches Backend) |
| `JWT_SECRET` | Yes | User Generated | Secret key matching Backend JWT secret |
| `DB_URL` | Yes | MySQL Provider | `mysql+pymysql://user:pass@host:port/dbname?ssl_ca=...` |
| `DB_POOL_SIZE` | Optional | App Config | Default `5` |
| `REDIS_HOST` | Yes | Redis Provider | Redis hostname |
| `REDIS_PORT` | Yes | Redis Provider | Redis port (`6379`) |
| `REDIS_PASSWORD` | Optional | Redis Provider | Redis auth password |
| `REDIS_SSL` | Yes | Redis Provider | `true` |
| `CORS_ORIGINS` | Optional | App Config | Empty or `["https://api.instantdonor.online"]` |

### Service 3: Frontend (`instantdonor.online`)

| Variable | Required | Source / Provider | Description / Example |
| :--- | :---: | :--- | :--- |
| `VITE_API_BASE_URL` | Yes | Build-Time (Vercel) | `https://api.instantdonor.online/api` |

---

## 5. Cold-Start & Free Tier Operational Notes

1. **Inactivity Sleep**: Render free instances spin down after 15 minutes of inactivity. Initial request wake-up requires 30–50 seconds.
2. **AI Service Resilience**: If the AI service is cold or sleeping when a blood request is created, the Backend's `AiDonorMatchingStrategy` retries 3 times before seamlessly falling back to direct database city-compatible donor matching (`findByCityFallback`). The emergency notification workflow never drops requests.
3. **Keep-Alive (Optional)**: For critical demo presentations, a free ping tool (e.g. UptimeRobot or cron job) can ping `/actuator/health` and `/health` every 10 minutes to prevent container sleep.
