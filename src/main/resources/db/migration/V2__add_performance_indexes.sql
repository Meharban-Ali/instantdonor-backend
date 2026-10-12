-- V2__add_performance_indexes.sql
-- Performance Indexes and Constraints for AiBloodDonor Network

-- 1. Enforce unique phone numbers on users
ALTER TABLE users 
    ADD CONSTRAINT uq_users_phone UNIQUE (phone);

-- 2. Composite index on donor_profiles for high-frequency city + blood group + availability search
CREATE INDEX idx_donor_city_group_avail 
    ON donor_profiles (city, blood_group, is_available);

-- 3. Composite coordinate index on donor_profiles for nearby Haversine radius searches
CREATE INDEX idx_donor_coords 
    ON donor_profiles (latitude, longitude);

-- 4. Composite index on blood_requests for filtered active requests query
CREATE INDEX idx_br_status_group_created_exp 
    ON blood_requests (status, blood_group, created_at, expired_at);

-- 5. Composite coordinate index on blood_requests for location tracking
CREATE INDEX idx_br_coords 
    ON blood_requests (latitude, longitude);
