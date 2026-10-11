# 🩸 AI Blood Donor Network

> Production-level AI-powered emergency blood donor platform that connects
> patients with compatible donors in minutes using smart matching algorithms.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![Python](https://img.shields.io/badge/Python-3.11-yellow)
![License](https://img.shields.io/badge/License-MIT-purple)

---

## 🚀 Features

- **AI-Powered Donor Matching** — Ranks donors by distance, reliability,
  eligibility and response rate using a weighted scoring algorithm
- **Emergency Notifications** — Email + SMS alerts to nearby donors
  within seconds of a blood request
- **Blood Compatibility Engine** — Automatically finds compatible donors
  when exact blood group unavailable
- **Real-time Status Tracking** — ACTIVE → MATCHING → FULFILLED lifecycle
- **Admin Dashboard** — Live analytics, charts, user/donor management
- **JWT Authentication** — Secure role-based access (USER, DONOR, ADMIN, HOSPITAL)
- **Redis Caching** — Sub-millisecond response for donor search
- **Auto-Expire Scheduler** — Requests auto-expire based on urgency level

---

## 🏗️ Architecture

┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   React Frontend │────▶│  Spring Boot API  │────▶│  Python FastAPI  │
│   (Port 5173)    │     │   (Port 8080)     │     │   (Port 8000)    │
│   Vite + Tailwind│     │   JWT + Security  │     │   AI Matching    │
└─────────────────┘     └────────┬─────────┘     └──────────────────┘
│
┌─────────────┼─────────────┐
│             │             │
┌──────▼──────┐ ┌───▼────┐ ┌─────▼─────┐
│   MySQL 8   │ │ Redis  │ │  Gmail    │
│  (Port 3306)│ │(Port   │ │  SMTP     │
└─────────────┘ │ 6379)  │ └───────────┘
└────────┘

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 17 LTS | Core language |
| Spring Boot | 3.2.5 | Web framework |
| Spring Security | 6.x | Auth + JWT |
| Spring Data JPA | 3.x | ORM |
| MySQL | 8.0 | Primary database |
| Redis | 7.x | Caching layer |
| Flyway | 10.x | DB migrations |

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| React | 18.x | UI framework |
| Vite | 5.x | Build tool |
| Tailwind CSS | 3.x | Styling |
| TanStack Query | 5.x | Server state |
| Zustand | 4.x | Client state |
| Framer Motion | 11.x | Animations |
| Recharts | 2.x | Charts |

### AI Service
| Technology | Version | Purpose |
|-----------|---------|---------|
| Python | 3.11 | Core language |
| FastAPI | 0.109 | API framework |
| SQLAlchemy | 2.0 | DB ORM |
| scikit-learn | 1.4 | ML library |
| NumPy | 1.26 | Math operations |

---

## ⚡ Quick Start

### Prerequisites
- Java 17+
- Node.js 20+
- Python 3.11+
- MySQL 8.0+
- Redis 7+

### 1. Clone the repository
```bash
git clone https://github.com/meharban-ali/ai-blood-donor-network.git
cd ai-blood-donor-network
```

### 2. Setup environment variables
```bash
cp .env.example .env
# Edit .env with your credentials
```

### 3. Start Backend
```bash
cd Backend/AiBloodDonorWebApplication_Backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### 4. Start AI Service
```bash
cd ai-service
python -m venv venv
venv\Scripts\activate  # Windows
pip install -r requirements.txt
python main.py
# Runs on http://localhost:8000
```

### 5. Start Frontend
```bash
cd frontend
pnpm install
pnpm dev
# Runs on http://localhost:5173
```

### 6. Or use Docker (recommended)
```bash
docker-compose up -d
# Full stack at http://localhost
```

---

## 🔑 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@blooddonor.com | Admin@1234 |

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/forgot-password` | Request reset email |
| POST | `/api/auth/reset-password` | Reset with token |
| GET  | `/api/auth/me` | Get my profile |

### Donors
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/donor/register` | Register as donor |
| GET  | `/api/donor/search` | Search by city |
| GET  | `/api/donor/search/nearby` | Search by location |
| PUT  | `/api/donor/availability` | Toggle availability |
| GET  | `/api/donor/eligibility` | Check 90-day rule |

### Blood Requests
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/request` | Create emergency request |
| GET  | `/api/request/active` | All active requests |
| GET  | `/api/request/:id` | Request detail |
| POST | `/api/request/respond` | Donor accept/reject |
| PUT  | `/api/request/:id/fulfill` | Mark fulfilled |

### AI Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/match-donors` | Find best donors |
| POST | `/api/ai/predict-demand` | Blood demand forecast |

📖 Full Swagger docs: `http://localhost:8080/swagger-ui.html`

---

## 🧠 AI Matching Algorithm
Donor Score (0-100) =
Distance Score    × 40%  (nearest = highest)
Reliability Score × 30%  (history based)
Eligibility Score × 20%  (90-day rule)
Response Rate     × 10%  (past behavior)

---

## 📁 Project Structure
ai-blood-donor-network/
├── Backend/
│   └── AiBloodDonorWebApplication_Backend/
│       ├── src/main/java/com/blooddonor/
│       │   ├── entity/          # 6 JPA entities
│       │   ├── repository/      # 6 repositories
│       │   ├── modules/         # 7 feature modules
│       │   ├── config/          # App configuration
│       │   ├── security/        # JWT security
│       │   └── scheduler/       # Scheduled tasks
│       └── src/main/resources/
│           ├── application.properties
│           └── templates/email/ # HTML email templates
├── ai-service/                  # Python FastAPI
│   ├── main.py
│   ├── services/
│   ├── routes/
│   └── utils/
├── frontend/                    # React + Vite
│   └── src/
│       ├── pages/
│       ├── components/
│       ├── hooks/
│       ├── services/
│       └── store/
├── docker-compose.yml
└── README.md


---

## 🔒 Security Features

- JWT access tokens (24h) + refresh tokens (7d)
- BCrypt password encoding (cost factor 12)
- Role-based access control (USER, DONOR, ADMIN, HOSPITAL)
- Password reset tokens expire in 30 minutes (one-time use)
- Enumeration attack prevention on forgot-password
- CORS configured for specific origins
- SQL injection prevention (JPA parameterized queries)
- XSS protection headers (Nginx)

---

## 🧪 Running Tests

```bash
# Backend tests
cd Backend/AiBloodDonorWebApplication_Backend
mvn test

# Frontend tests
cd frontend
pnpm run test
```

---

## 📄 License

MIT License — see LICENSE file for details.