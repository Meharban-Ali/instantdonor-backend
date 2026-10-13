import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const loginDuration = new Trend('login_duration_ms', true);
const loginErrorRate = new Rate('login_error_rate');
const loginCount = new Counter('login_total_count');

export const options = {
  insecureSkipTLSVerify: true,
  scenarios: {
    dedicated_login_load: {
      executor: 'constant-vus',
      vus: 20,
      duration: '20s',
    },
  },
  thresholds: {
    'login_duration_ms': ['p(95)<4000'],
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'https://nginx:443';

export function setup() {
  console.log(`[Setup] Pre-seeding login users against ${BASE_URL}...`);
  const users = [];
  for (let i = 1; i <= 20; i++) {
    const email = `bench_user_${i}_${Date.now()}@loadtest.local`;
    const password = 'Password@123!';
    const phone = `95${String(10000000 + i)}`;

    const res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
      name: `Bench User ${i}`,
      email: email,
      password: password,
      phone: phone,
    }), {
      headers: { 'Content-Type': 'application/json', 'Host': 'localhost' },
      insecureSkipTLSVerify: true,
    });

    if (res.status === 201) {
      users.push({ email, password });
    }
  }
  console.log(`[Setup] Successfully registered ${users.length} users.`);
  return { users };
}

export default function (data) {
  if (!data.users || data.users.length === 0) return;
  const user = data.users[__VU % data.users.length];

  const startTime = new Date().getTime();
  const res = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
    email: user.email,
    password: user.password,
  }), {
    headers: { 'Content-Type': 'application/json', 'Host': 'localhost' },
    insecureSkipTLSVerify: true,
  });

  const duration = new Date().getTime() - startTime;
  loginDuration.add(duration);
  loginCount.add(1);

  const success = res.status === 200;
  loginErrorRate.add(!success);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(0.1);
}
