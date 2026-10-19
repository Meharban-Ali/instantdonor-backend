# Security Policy

## Supported Versions

Only the latest release branch is actively maintained with security updates.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0.0 | :x:                |

## Reporting a Vulnerability

If you discover a potential security vulnerability within this repository, please do **NOT** create a public issue.

Instead, please report security vulnerabilities responsibly:

1. **Email:** Send details to `security@blooddonor.com` or `meharbanali7900@gmail.com`.
2. **Details to include:**
   - Nature of the vulnerability (e.g. authentication bypass, SQL injection, IDOR, data exposure).
   - Step-by-step reproduction instructions or proof-of-concept payload.
   - Affected endpoints or components.
3. **Response Timeline:**
   - Acknowledgement within **24–48 hours**.
   - Remediation and patch deployment plan within **5 business days**.

## Security Guidelines

- Never commit plain-text credentials, passwords, JWT secrets, or production connection strings to version control.
- Use environment variables to inject secrets at runtime.
- In production, minimum key lengths are strictly enforced (e.g., minimum 32-character AI API key, minimum 256-bit JWT secret).
