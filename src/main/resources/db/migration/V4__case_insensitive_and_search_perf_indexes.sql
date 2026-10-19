-- V4__case_insensitive_and_search_perf_indexes.sql
-- Optimizations identified via EXPLAIN (ANALYZE, BUFFERS) for case-insensitive city search,
-- donor availability pre-filtering, and active request prioritization

-- 1. Functional composite index for case-insensitive city search on donor_profiles
CREATE INDEX idx_donor_lower_city_group_avail 
    ON donor_profiles (LOWER(city), blood_group, is_available);

-- 2. Availability + blood group composite index for fast candidate pre-filtering before Haversine distance math
CREATE INDEX idx_donor_avail_blood_group 
    ON donor_profiles (is_available, blood_group);

-- 3. Composite functional index for active requests query with status, urgency, case-insensitive city, and time ordering
CREATE INDEX idx_br_status_urgency_lower_city 
    ON blood_requests (status, urgency, LOWER(city), created_at DESC);
