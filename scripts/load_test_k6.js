import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// Custom metrics
const loginDuration = new Trend('login_duration_ms');
const donorSearchDuration = new Trend('donor_search_duration_ms');
const requestCreateDuration = new Trend('request_create_duration_ms');
const failureRate = new Rate('custom_failure_rate');

export const options = {
  stages: [
    { duration: '10s', target: 20 },   // Warm-up to 20 VUs
    { duration: '30s', target: 100 },  // Ramp-up to 100 concurrent VUs
    { duration: '60s', target: 100 },  // Sustained load with 100 concurrent VUs
    { duration: '10s', target: 0 },    // Ramp-down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500'], // 95% of requests must complete below 500ms
    'donor_search_duration_ms': ['p(95)<300'], // Search should be fast with index
    'custom_failure_rate': ['rate<0.02'], // Error rate under 2%
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080';

export default function () {
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  // 1. User Login
  const loginPayload = JSON.stringify({
    email: 'admin@blooddonor.org',
    password: __ENV.ADMIN_PASSWORD || 'Admin@12345!',
  });

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, { headers });
  loginDuration.add(loginRes.timings.duration);
  const loginOk = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login has token': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.token;
      } catch (e) {
        return false;
      }
    },
  });

  if (!loginOk) {
    failureRate.add(1);
  } else {
    failureRate.add(0);
    const token = JSON.parse(loginRes.body).data.token;
    const authHeaders = {
      ...headers,
      'Authorization': `Bearer ${token}`,
    };

    // 2. Donor Search (indexed query)
    const searchRes = http.get(`${BASE_URL}/api/donor/search?bloodGroup=O_POSITIVE&city=Delhi`, {
      headers: authHeaders,
    });
    donorSearchDuration.add(searchRes.timings.duration);
    const searchOk = check(searchRes, {
      'donor search status is 200': (r) => r.status === 200,
    });
    if (!searchOk) {
      failureRate.add(1);
    }

    // 3. Blood Request Creation
    const requestPayload = JSON.stringify({
      patientName: 'Test Patient',
      bloodGroup: 'O_POSITIVE',
      unitsRequired: 2,
      hospitalName: 'Apollo Hospital',
      city: 'Delhi',
      latitude: 28.6139,
      longitude: 77.2090,
      urgency: 'URGENT',
      contactPhone: '9876543210',
    });

    const createRes = http.post(`${BASE_URL}/api/request`, requestPayload, {
      headers: authHeaders,
    });
    requestCreateDuration.add(createRes.timings.duration);
    const createOk = check(createRes, {
      'request create status is 201 or 200': (r) => r.status === 201 || r.status === 200,
    });
    if (!createOk) {
      failureRate.add(1);
    }
  }

  sleep(1);
}
