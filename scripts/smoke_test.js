// scripts/smoke_test.js
const http = require('http');

async function request(options, body) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => resolve({ status: res.statusCode, data }));
    });
    req.on('error', reject);
    if (body) req.write(body);
    req.end();
  });
}

async function runSmokeTest() {
  const rand = Math.floor(Math.random() * 100000);
  const email = `smoke_${rand}@example.com`;
  const phone = `98${Math.floor(10000000 + Math.random() * 89999999)}`;

  console.log(`=== RUNNING SMOKE TEST ON RUNNING DOCKER CONTAINER (port 8090) ===`);
  console.log(`Step 1: Registering user ${email}`);

  const regBody = JSON.stringify({
    name: "Smoke Test User",
    email: email,
    password: "SmokePassword@123!",
    phone: phone
  });

  const regRes = await request({
    hostname: 'localhost',
    port: 8090,
    path: '/api/auth/register',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(regBody)
    }
  }, regBody);

  console.log(`Register status: ${regRes.status}`);
  console.log(`Register body: ${regRes.data}`);

  console.log(`\nStep 2: Logging in as ${email}`);
  const loginBody = JSON.stringify({
    email: email,
    password: "SmokePassword@123!"
  });

  const loginRes = await request({
    hostname: 'localhost',
    port: 8090,
    path: '/api/auth/login',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(loginBody)
    }
  }, loginBody);

  console.log(`Login status: ${loginRes.status}`);
  console.log(`Login body: ${loginRes.data}`);

  const parsedLogin = JSON.parse(loginRes.data);
  const token = parsedLogin.data ? parsedLogin.data.accessToken : parsedLogin.accessToken;
  console.log(`JWT Token extracted: ${token ? token.substring(0, 30) + '...' : 'NONE'}`);

  console.log(`\nStep 3: Searching donors (bloodGroup=O_POSITIVE, city=Delhi)`);
  const searchRes = await request({
    hostname: 'localhost',
    port: 8090,
    path: '/api/donor/search?bloodGroup=O_POSITIVE&city=Delhi',
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  console.log(`Search status: ${searchRes.status}`);
  console.log(`Search body: ${searchRes.data}`);

  console.log(`\n=== SMOKE TEST SUMMARY ===`);
  if ((regRes.status === 200 || regRes.status === 201) && loginRes.status === 200 && searchRes.status === 200) {
    console.log(`ALL 3 SMOKE TESTS PASSED (Register: ${regRes.status}, Login: ${loginRes.status}, Search: ${searchRes.status})`);
  } else {
    console.error(`SMOKE TEST FAILED!`);
    process.exit(1);
  }
}

runSmokeTest().catch(err => {
  console.error("Test execution failed:", err);
  process.exit(1);
});
