import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

// Custom Metrics for Endpoint-wise latency profiling
const loginDuration = new Trend('login_duration_ms', true);
const searchDuration = new Trend('search_duration_ms', true);
const requestCreateDuration = new Trend('request_create_duration_ms', true);

const loginErrors = new Counter('login_errors_count');
const loginRateLimited = new Counter('login_429_count');
const loginSuccesses = new Counter('login_success_count');

const searchErrors = new Counter('search_errors_count');
const searchSuccesses = new Counter('search_success_count');

const requestCreateErrors = new Counter('request_create_errors_count');
const requestCreateSuccesses = new Counter('request_create_success_count');

export const options = {
  scenarios: {
    mixed_workload: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '10s', target: 50 },  // Ramp-up to 50 VUs
        { duration: '20s', target: 100 }, // Ramp-up to 100 concurrent VUs
        { duration: '30s', target: 100 }, // Sustained load at 100 VUs
        { duration: '10s', target: 0 },   // Cool-down
      ],
      gracefulRampDown: '5s',
    },
  },
  thresholds: {
    'search_duration_ms': ['p(95)<300'], // Database index + Redis cache should serve search < 300ms
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://blood_donor_backend:8080';
const JSON_HEADERS = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Host': 'localhost:8080',
};

// Setup phase: Pre-seed test users and obtain auth tokens for search & request operations
export function setup() {
  console.log(`Setting up test benchmark pool against ${BASE_URL}...`);
  const pool = [];
  const poolSize = 30; // 30 separate users to distribute requests and respect per-user rules

  for (let i = 1; i <= poolSize; i++) {
    const email = `k6_bench_${i}_${Date.now()}@benchmark.local`;
    const password = 'Password@123!';
    const phone = `+9198${String(10000000 + i)}`;

    // 1. Register User
    const regPayload = JSON.stringify({
      name: `Benchmark User ${i}`,
      email: email,
      password: password,
      phone: phone,
    });
    const regRes = http.post(`${BASE_URL}/api/auth/register`, regPayload, { headers: JSON_HEADERS });

    let token = null;
    let userId = null;
    if (regRes.status === 200 || regRes.status === 201) {
      try {
        const body = JSON.parse(regRes.body);
        token = body.data?.accessToken;
        userId = body.data?.userId;
      } catch (e) {}
    }

    if (!token) {
      const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({ email, password }), { headers: JSON_HEADERS });
      if (loginRes.status === 200) {
        try {
          const body = JSON.parse(loginRes.body);
          token = body.data?.accessToken;
          userId = body.data?.userId;
        } catch (e) {}
      }
    }

    // Seed 10 donors in Delhi with O_POSITIVE so search query returns actual indexed records
    if (token && i <= 10) {
      const donorPayload = JSON.stringify({
        bloodGroup: 'O_POSITIVE',
        location: {
          city: 'Delhi',
          state: 'Delhi',
          latitude: 28.6139 + (i * 0.001),
          longitude: 77.2090 + (i * 0.001),
        },
        lastDonationDate: '2026-05-01',
      });
      http.post(`${BASE_URL}/api/donor/profile`, donorPayload, {
        headers: { ...JSON_HEADERS, 'Authorization': `Bearer ${token}` },
      });
    }

    pool.push({ email, password, token, userId });
  }

  console.log(`Setup complete. Seeded pool of ${pool.length} benchmark users.`);
  return { pool };
}

export default function (data) {
  const pool = data.pool;
  if (!pool || pool.length === 0) {
    return;
  }

  // Workload Mix:
  // 20% Login attempts
  // 60% Donor Search queries (cached & indexed)
  // 20% Blood Request Create / Read operations
  const roll = Math.random();
  const user = pool[__VU % pool.length];

  if (roll < 0.20) {
    // ──────────────────────────────────────────────
    // 1. LOGIN ENDPOINT (20% Mix) - Tests BCrypt CPU cost & Rate Limiting
    // ──────────────────────────────────────────────
    const start = Date.now();
    const loginPayload = JSON.stringify({
      email: user.email,
      password: user.password,
    });

    const res = http.post(`${BASE_URL}/api/auth/login`, loginPayload, { headers: JSON_HEADERS });
    const duration = Date.now() - start;
    loginDuration.add(duration);

    if (res.status === 200) {
      loginSuccesses.add(1);
    } else if (res.status === 429) {
      // Rate limiting cleanly handled: Expected behavior under rapid repeated logins per IP/email
      loginRateLimited.add(1);
    } else {
      loginErrors.add(1);
    }
  } else if (roll < 0.80) {
    // ──────────────────────────────────────────────
    // 2. DONOR SEARCH ENDPOINT (60% Mix) - Tests @EntityGraph, DB Index & Redis
    // ──────────────────────────────────────────────
    const authHeaders = {
      ...JSON_HEADERS,
      'Authorization': `Bearer ${user.token}`,
    };

    const start = Date.now();
    const res = http.get(
      `${BASE_URL}/api/donor/search?bloodGroup=O_POSITIVE&city=Delhi&page=0&size=20`,
      { headers: authHeaders }
    );
    const duration = Date.now() - start;
    searchDuration.add(duration);

    if (res.status === 200) {
      searchSuccesses.add(1);
    } else {
      searchErrors.add(1);
    }
  } else {
    // ──────────────────────────────────────────────
    // 3. BLOOD REQUEST ENDPOINT (20% Mix) - Tests Transaction & Active Limits
    // ──────────────────────────────────────────────
    const authHeaders = {
      ...JSON_HEADERS,
      'Authorization': `Bearer ${user.token}`,
    };

    const reqPayload = JSON.stringify({
      patient: {
        patientName: `Patient VU-${__VU}`,
        contactPhone: `+9198${String(20000000 + (__VU % 1000))}`,
      },
      hospital: {
        hospitalName: 'Apollo Hospital',
        city: 'Delhi',
        latitude: 28.6139,
        longitude: 77.2090,
      },
      config: {
        bloodGroup: 'O_POSITIVE',
        unitsRequired: 2,
        urgency: 'URGENT',
      },
    });

    const start = Date.now();
    const res = http.post(`${BASE_URL}/api/request`, reqPayload, { headers: authHeaders });
    const duration = Date.now() - start;
    requestCreateDuration.add(duration);

    // 201 Created is success; 409 Conflict represents duplicate active request prevention
    if (res.status === 201 || res.status === 200 || res.status === 409) {
      requestCreateSuccesses.add(1);
    } else {
      requestCreateErrors.add(1);
    }
  }

  // Pacing: 250-500ms think time per VU
  sleep(0.25 + Math.random() * 0.25);
}
