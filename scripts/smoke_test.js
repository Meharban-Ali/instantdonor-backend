// scripts/smoke_test.js
const http = require('http');

const PORT = parseInt(process.env.APP_PORT || '8090', 10);
const HOST = process.env.APP_HOST || 'localhost';

async function request(options, body) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          const json = data ? JSON.parse(data) : null;
          resolve({ status: res.statusCode, headers: res.headers, raw: data, data: json });
        } catch (e) {
          resolve({ status: res.statusCode, headers: res.headers, raw: data, data: null });
        }
      });
    });
    req.on('error', reject);
    if (body) req.write(body);
    req.end();
  });
}

async function runSmokeTest() {
  const timestamp = Date.now();
  const donorEmail = `donor_${timestamp}@example.com`;
  const requesterEmail = `requester_${timestamp}@example.com`;
  const donorPhone = `98${Math.floor(10000000 + Math.random() * 89999999)}`;
  const requesterPhone = `97${Math.floor(10000000 + Math.random() * 89999999)}`;

  console.log(`===============================================================`);
  console.log(`  RUNNING COMPREHENSIVE DOCKER SMOKE TEST (Target: ${HOST}:${PORT})`);
  console.log(`===============================================================\n`);

  // -------------------------------------------------------------
  // STEP 1: DONOR REGISTER
  // -------------------------------------------------------------
  console.log(`[Step 1] Donor Registration Flow:`);
  console.log(`  - Registering user: ${donorEmail}`);
  const regBody = JSON.stringify({
    name: "Aarav Sharma (Donor)",
    email: donorEmail,
    password: "Password@123!",
    phone: donorPhone
  });

  const regRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/auth/register',
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(regBody) }
  }, regBody);
  console.log(`  - User Registration Status: ${regRes.status} (Success: ${regRes.data?.success})`);

  console.log(`  - Logging in as Donor...`);
  const loginBody = JSON.stringify({ email: donorEmail, password: "Password@123!" });
  const loginRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/auth/login',
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(loginBody) }
  }, loginBody);
  const donorToken = loginRes.data?.data?.accessToken;
  console.log(`  - Login Status: ${loginRes.status}, Token: ${donorToken ? donorToken.substring(0, 25) + '...' : 'NONE'}`);

  console.log(`  - Registering Donor Profile (O_POSITIVE, Delhi)...`);
  const donorProfBody = JSON.stringify({
    bloodGroup: "O_POSITIVE",
    location: {
      city: "Delhi",
      state: "Delhi",
      latitude: 28.6139,
      longitude: 77.2090
    }
  });
  const donorProfRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/donor/register',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(donorProfBody),
      'Authorization': `Bearer ${donorToken}`
    }
  }, donorProfBody);
  console.log(`  - Donor Profile Status: ${donorProfRes.status} (DonorId: ${donorProfRes.data?.data?.donorId})`);

  // -------------------------------------------------------------
  // STEP 2: SEARCH DONORS & VERIFY PHONE IS MASKED
  // -------------------------------------------------------------
  console.log(`\n[Step 2] Public Donor Search & PII Masking Verification:`);
  const searchRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/donor/search?city=Delhi&bloodGroup=O_POSITIVE',
    method: 'GET'
  });
  console.log(`  - Search Status: ${searchRes.status}, Total Found: ${searchRes.data?.data?.totalElements}`);
  
  const donors = searchRes.data?.data?.content || [];
  const foundDonor = donors.find(d => d.donorId === donorProfRes.data?.data?.donorId) || donors[0];
  const returnedPhone = foundDonor?.user?.phone || 'NOT_FOUND';
  const isMasked = returnedPhone.includes('*');
  console.log(`  - Returned Phone for donor: "${returnedPhone}"`);
  console.log(`  - Phone is masked with asterisks: ${isMasked ? 'YES (Verified PII Safe)' : 'NO (FAILED!)'}`);

  // -------------------------------------------------------------
  // STEP 3: BLOOD REQUEST CREATE (BY A REQUESTER)
  // -------------------------------------------------------------
  console.log(`\n[Step 3] Blood Request Creation Flow:`);
  console.log(`  - Registering Requester User: ${requesterEmail}`);
  const reqRegBody = JSON.stringify({
    name: "Priya Patel (Requester)",
    email: requesterEmail,
    password: "Password@123!",
    phone: requesterPhone
  });
  const reqRegRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/auth/register',
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(reqRegBody) }
  }, reqRegBody);

  const reqLoginRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/auth/login',
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(JSON.stringify({ email: requesterEmail, password: "Password@123!" })) }
  }, JSON.stringify({ email: requesterEmail, password: "Password@123!" }));
  const requesterToken = reqLoginRes.data?.data?.accessToken;

  console.log(`  - Creating Emergency Blood Request...`);
  const bloodReqBody = JSON.stringify({
    patient: {
      patientName: "Patient Sharma",
      contactPhone: requesterPhone
    },
    hospital: {
      hospitalName: "AIIMS New Delhi",
      city: "Delhi",
      latitude: 28.5672,
      longitude: 77.2100
    },
    config: {
      bloodGroup: "O_POSITIVE",
      unitsRequired: 2,
      urgency: "CRITICAL"
    }
  });

  const bloodReqRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/request',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(bloodReqBody),
      'Authorization': `Bearer ${requesterToken}`
    }
  }, bloodReqBody);

  const createdRequestId = bloodReqRes.data?.data?.id;
  console.log(`  - Blood Request Created: Status ${bloodReqRes.status}, RequestId: ${createdRequestId}, Urgency: ${bloodReqRes.data?.data?.urgency}`);

  // -------------------------------------------------------------
  // STEP 4: DONOR RESPONDS TO BLOOD REQUEST
  // -------------------------------------------------------------
  console.log(`\n[Step 4] Donor Response Flow:`);
  console.log(`  - Donor (Aarav Sharma) accepting Request #${createdRequestId}...`);
  const respondBody = JSON.stringify({
    requestId: createdRequestId,
    accept: true,
    message: "I am ready to donate at AIIMS"
  });

  const respondRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/request/respond',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(respondBody),
      'Authorization': `Bearer ${donorToken}`
    }
  }, respondBody);
  console.log(`  - Respond Status: ${respondRes.status}, Status Message: "${respondRes.data?.message}"`);

  // -------------------------------------------------------------
  // STEP 5: LOGOUT
  // -------------------------------------------------------------
  console.log(`\n[Step 5] Logout Flow:`);
  const logoutRes = await request({
    hostname: HOST,
    port: PORT,
    path: '/api/auth/logout',
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${donorToken}`
    }
  });
  console.log(`  - Logout Status: ${logoutRes.status}, Message: "${logoutRes.data?.message || logoutRes.raw}"`);

  // -------------------------------------------------------------
  // SUMMARY
  // -------------------------------------------------------------
  console.log(`\n===============================================================`);
  console.log(`  SMOKE TEST VERIFICATION RESULTS`);
  console.log(`===============================================================`);
  const step1Ok = (regRes.status === 201 || regRes.status === 200) && donorProfRes.status === 201;
  const step2Ok = searchRes.status === 200 && isMasked;
  const step3Ok = bloodReqRes.status === 201 && createdRequestId != null;
  const step4Ok = respondRes.status === 200;
  const step5Ok = logoutRes.status === 200;

  console.log(`Step 1 (Donor Register):       ${step1Ok ? 'PASS [201 Created]' : 'FAIL [' + donorProfRes.status + ']'}`);
  console.log(`Step 2 (Search & Masked Phone):${step2Ok ? 'PASS [200 OK, Phone Masked]' : 'FAIL'}`);
  console.log(`Step 3 (Blood Request Create): ${step3Ok ? 'PASS [201 Created, ID=' + createdRequestId + ']' : 'FAIL'}`);
  console.log(`Step 4 (Donor Respond):        ${step4Ok ? 'PASS [200 OK, Accepted]' : 'FAIL [' + respondRes.status + ']'}`);
  console.log(`Step 5 (Logout):               ${step5Ok ? 'PASS [200 OK]' : 'FAIL [' + logoutRes.status + ']'}`);

  if (step1Ok && step2Ok && step3Ok && step4Ok && step5Ok) {
    console.log(`\n>>> ALL 5 SMOKE TEST STEPS PASSED SUCCESSFULLY! <<<`);
    process.exit(0);
  } else {
    console.error(`\n>>> SMOKE TEST FAILED ON ONE OR MORE STEPS! <<<`);
    process.exit(1);
  }
}

runSmokeTest().catch(err => {
  console.error("Test execution failed with error:", err);
  process.exit(1);
});
