import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';

// Dedicated Latency Trends per Endpoint
const loginDuration = new Trend('login_duration_ms', true);
const searchDuration = new Trend('search_duration_ms', true);
const requestCreateDuration = new Trend('request_create_duration_ms', true);

// Endpoint-wise Error Rates
const loginErrorRate = new Rate('login_error_rate');
const searchErrorRate = new Rate('search_error_rate');
const requestCreateErrorRate = new Rate('request_create_error_rate');

// Counters
const loginSuccesses = new Counter('login_success_count');
const searchSuccesses = new Counter('search_success_count');
const requestCreateSuccesses = new Counter('request_create_success_count');

export const options = {
  scenarios: {
    production_workload: {
      executor: 'ramping-vus',
      startVUs: 5,
      stages: [
        { duration: '10s', target: 25 },  // Ramp-up to 25 concurrent VUs
        { duration: '20s', target: 50 },  // Ramp-up to 50 concurrent VUs
        { duration: '20s', target: 50 },  // Sustained load at 50 VUs
        { duration: '10s', target: 0 },   // Graceful cool-down
      ],
      gracefulRampDown: '5s',
    },
  },
  thresholds: {
    'search_duration_ms': ['p(95)<300'],
    'login_duration_ms': ['p(95)<2500'], // BCrypt CPU load threshold
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://backend:8080';
const JSON_HEADERS = {
  'Content-Type': 'application/json',
  'Accept': 'application/json',
};

// -------------------------------------------------------------
// SETUP: Pre-seed 60 unique accounts (1 dedicated account per VU)
// -------------------------------------------------------------
export function setup() {
  console.log(`[k6 Setup] Seeding unique per-VU accounts against ${BASE_URL}...`);
  const pool = [];
  const poolSize = 60; // 1 dedicated user per VU up to 50-60 VUs

  for (let i = 1; i <= poolSize; i++) {
    const email = `k6_user_${i}_${Date.now()}@loadtest.local`;
    const password = 'Password@123!';
    const phone = `96${String(10000000 + i)}`;

    // 1. Register User
    const regPayload = JSON.stringify({
      name: `LoadTest User ${i}`,
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

    // Fallback: Login if register didn't return token
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

    // Seed 10 donors in Delhi with O_POSITIVE so search returns indexed records
    if (token && i <= 10) {
      const donorPayload = JSON.stringify({
        bloodGroup: 'O_POSITIVE',
        location: {
          city: 'Delhi',
          state: 'Delhi',
          latitude: 28.6139 + (i * 0.001),
          longitude: 77.2090 + (i * 0.001),
        },
      });
      http.post(`${BASE_URL}/api/donor/register`, donorPayload, {
        headers: { ...JSON_HEADERS, 'Authorization': `Bearer ${token}` },
      });
    }

    pool.push({ email, password, token, userId });
  }

  console.log(`[k6 Setup] Successfully pre-seeded pool of ${pool.length} isolated accounts.`);
  return { pool };
}

export default function (data) {
  const pool = data.pool;
  if (!pool || pool.length === 0) return;

  // Strict Per-VU user isolation: VU index maps to dedicated user in pool
  const vuIndex = (__VU - 1) % pool.length;
  const user = pool[vuIndex];

  // Workload Mix:
  // 20% Login (Tests BCrypt CPU cost under concurrency)
  // 60% Donor Search (Tests DB Index & Redis Caching)
  // 20% Blood Request Operations (Tests Transactions & Quotas with unique user per VU)
  const roll = Math.random();

  if (roll < 0.20) {
    // ---------------------------------------------------------
    // 1. LOGIN ENDPOINT (20% Mix) - Measures Pure BCrypt CPU Latency
    // ---------------------------------------------------------
    const start = Date.now();
    const loginPayload = JSON.stringify({
      email: user.email,
      password: user.password,
    });

    const res = http.post(`${BASE_URL}/api/auth/login`, loginPayload, { headers: JSON_HEADERS });
    const duration = Date.now() - start;
    loginDuration.add(duration);

    const isOk = res.status === 200;
    loginErrorRate.add(isOk ? 0 : 1);
    if (isOk) loginSuccesses.add(1);

  } else if (roll < 0.80) {
    // ---------------------------------------------------------
    // 2. DONOR SEARCH ENDPOINT (60% Mix) - Tests Index & Redis Cache
    // ---------------------------------------------------------
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

    const isOk = res.status === 200;
    searchErrorRate.add(isOk ? 0 : 1);
    if (isOk) searchSuccesses.add(1);

  } else {
    // ---------------------------------------------------------
    // 3. BLOOD REQUEST ENDPOINT (20% Mix) - Isolated User per VU
    // ---------------------------------------------------------
    const authHeaders = {
      ...JSON_HEADERS,
      'Authorization': `Bearer ${user.token}`,
    };

    const reqPayload = JSON.stringify({
      patient: {
        patientName: `Patient VU-${__VU}`,
        contactPhone: `95${String(10000000 + (__VU % 10000))}`,
      },
      hospital: {
        hospitalName: 'AIIMS New Delhi',
        city: 'Delhi',
        latitude: 28.5672,
        longitude: 77.2100,
      },
      config: {
        bloodGroup: 'O_POSITIVE',
        unitsRequired: 1,
        urgency: 'URGENT',
      },
    });

    const start = Date.now();
    const res = http.post(`${BASE_URL}/api/request`, reqPayload, { headers: authHeaders });
    const duration = Date.now() - start;
    requestCreateDuration.add(duration);

    // 201 Created is success; 409 Conflict represents duplicate active request prevention (handled gracefully)
    const isSuccess = (res.status === 201 || res.status === 200 || res.status === 409);
    requestCreateErrorRate.add(isSuccess ? 0 : 1);
    if (isSuccess) requestCreateSuccesses.add(1);
  }

  // Think time: 200-400ms realistic pacing
  sleep(0.2 + Math.random() * 0.2);
}
