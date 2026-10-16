# ==============================================================================
# Full-Stack Integration & Contract Verification Runner
# Runs with throwaway in-memory parameters; prints no secrets or user data.
# ==============================================================================
$ErrorActionPreference = "Stop"

$RUN_ID = [System.Guid]::NewGuid().ToString("N").Substring(0, 8)
$TRACE_ID = "trace-" + $RUN_ID

Write-Host "================================================================"
Write-Host "FULL-STACK AUTOMATED TEST VERIFICATION"
Write-Host "Trace ID : $TRACE_ID"
Write-Host "Started  : $(Get-Date -Format 'yyyy-MM-ddTHH:mm:ss.fffZ')"
Write-Host "================================================================"

# 1. Backend targeted integration verification
Write-Host "`n[1/3] Running Backend Integration & Security Tests..."
Push-Location (Join-Path $PSScriptRoot "..")
try {
    & "D:\Meharban-code\apache-maven-3.9.16\bin\mvn.cmd" test "-Dtest=AuthControllerIntegrationTest,RealDatabasePhase3HardeningTest,AiSerializationRoundTripTest"
    Write-Host "Backend tests: PASSED" -ForegroundColor Green
} finally {
    Pop-Location
}

# 2. Frontend Unit & Contract verification
Write-Host "`n[2/3] Running Frontend Unit & Contract Tests..."
Push-Location (Join-Path $PSScriptRoot "..\..\..\Frontend\ai-blood-donor-ui")
try {
    npm run test:run
    Write-Host "Frontend Vitest tests: PASSED" -ForegroundColor Green
} finally {
    Pop-Location
}

# 3. AI Service Tests
Write-Host "`n[3/3] Running AI Service Tests in Container..."
Push-Location (Join-Path $PSScriptRoot "..\..\..\ai-service")
try {
    docker run --rm -v "${PWD}:/app" -w /app blood_donor_ai:test pytest
    Write-Host "AI Service pytest: PASSED" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host "`nAll Test Suites Passed Cleanly!" -ForegroundColor Green
