-- scripts/seed_demo_data.sql
-- Consistent demo data for development, testing, and smoke verification
-- Contains: 1 Admin, 3 Donors (distinct blood groups & locations), 2 Hospitals, 2 Active Requests

-- 1. Demo Users (BCrypt password: '$2a$10$DemoPasswordHashNotForProd123456789012345678901234567890')
-- Using placeholder hash for local dev/testing
INSERT INTO users (id, name, email, password, phone, is_active, created_at, updated_at)
VALUES 
  (1001, 'System Administrator', 'admin@blooddonor.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900001', true, NOW(), NOW()),
  (1002, 'Rohan Sharma', 'rohan.donor@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900002', true, NOW(), NOW()),
  (1003, 'Priya Patel', 'priya.donor@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900003', true, NOW(), NOW()),
  (1004, 'Amit Verma', 'amit.donor@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900004', true, NOW(), NOW()),
  (1005, 'AIIMS New Delhi Coordinator', 'aiims.hospital@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900005', true, NOW(), NOW()),
  (1006, 'Lilavati Hospital Staff', 'lilavati.hospital@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '9999900006', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. User Roles
INSERT INTO user_roles (user_id, role) VALUES
  (1001, 'ROLE_ADMIN'),
  (1001, 'ROLE_USER'),
  (1002, 'ROLE_DONOR'),
  (1002, 'ROLE_USER'),
  (1003, 'ROLE_DONOR'),
  (1003, 'ROLE_USER'),
  (1004, 'ROLE_DONOR'),
  (1004, 'ROLE_USER'),
  (1005, 'ROLE_HOSPITAL'),
  (1005, 'ROLE_USER'),
  (1006, 'ROLE_HOSPITAL'),
  (1006, 'ROLE_USER')
ON CONFLICT (user_id, role) DO NOTHING;

-- 3. Donor Profiles
-- Donor 1: Rohan Sharma (Delhi, O_POSITIVE, Available, Connaught Place lat/lng: 28.6315, 77.2167)
-- Donor 2: Priya Patel (Mumbai, A_POSITIVE, Available, Bandra lat/lng: 19.0596, 72.8295)
-- Donor 3: Amit Verma (Bengaluru, B_NEGATIVE, Unavailable, Indiranagar lat/lng: 12.9784, 77.6408)
INSERT INTO donor_profiles (
  id, user_id, blood_group, city, state, latitude, longitude,
  is_available, reliability_score, response_rate, total_donations,
  email_alerts_enabled, sms_alerts_enabled, consent_updated_at,
  created_at, updated_at
) VALUES
  (1001, 1002, 'O_POSITIVE', 'Delhi', 'Delhi', 28.63150000, 77.21670000, true, 95.00, 98.00, 5, true, true, NOW(), NOW(), NOW()),
  (1002, 1003, 'A_POSITIVE', 'Mumbai', 'Maharashtra', 19.05960000, 72.82950000, true, 90.00, 92.00, 3, true, true, NOW(), NOW(), NOW()),
  (1003, 1004, 'B_NEGATIVE', 'Bengaluru', 'Karnataka', 12.97840000, 77.64080000, false, 85.00, 80.00, 2, false, false, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 4. Blood Requests
-- Request 1: O_POSITIVE in Delhi, CRITICAL, created by Hospital 1 (AIIMS)
-- Request 2: A_POSITIVE in Mumbai, URGENT, created by Hospital 2 (Lilavati)
INSERT INTO blood_requests (
  id, created_by, patient_name, hospital_name, contact_phone, city,
  latitude, longitude, blood_group, units_required, urgency, status,
  created_at, expired_at, updated_at, version
) VALUES
  (1001, 1005, 'Ramesh Kumar', 'AIIMS New Delhi', '9999900005', 'Delhi', 28.56720000, 77.21000000, 'O_POSITIVE', 2, 'CRITICAL', 'ACTIVE', NOW(), NOW() + INTERVAL '24 hours', NOW(), 0),
  (1002, 1006, 'Sunita Rao', 'Lilavati Hospital', '9999900006', 'Mumbai', 19.05150000, 72.82870000, 'A_POSITIVE', 1, 'URGENT', 'ACTIVE', NOW(), NOW() + INTERVAL '48 hours', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- Reset identity sequence if needed
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1) + 1, false) FROM users;
SELECT setval(pg_get_serial_sequence('donor_profiles', 'id'), COALESCE(MAX(id), 1) + 1, false) FROM donor_profiles;
SELECT setval(pg_get_serial_sequence('blood_requests', 'id'), COALESCE(MAX(id), 1) + 1, false) FROM blood_requests;
