-- 1. Table users
CREATE TABLE users (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    created_at datetime(6) DEFAULT NULL,
    email varchar(150) NOT NULL UNIQUE,
    is_active bit(1) NOT NULL,
    last_login_at datetime(6) DEFAULT NULL,
    name varchar(100) NOT NULL,
    password varchar(255) NOT NULL,
    phone varchar(15) NOT NULL,
    updated_at datetime(6) DEFAULT NULL,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table user_roles
CREATE TABLE user_roles (
    user_id bigint NOT NULL,
    role enum('USER','DONOR','ADMIN','HOSPITAL') NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table donor_profiles
CREATE TABLE donor_profiles (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    blood_group enum('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    city varchar(50) NOT NULL,
    created_at datetime(6) DEFAULT NULL,
    is_available bit(1) NOT NULL,
    last_donation_date date DEFAULT NULL,
    latitude decimal(10,8) NOT NULL,
    longitude decimal(11,8) NOT NULL,
    next_eligible_date date DEFAULT NULL,
    reliability_score decimal(5,2) NOT NULL,
    response_rate decimal(5,2) NOT NULL,
    state varchar(50) NOT NULL,
    total_donations int NOT NULL,
    updated_at datetime(6) DEFAULT NULL,
    user_id bigint NOT NULL UNIQUE,
    CONSTRAINT fk_dp_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_donor_user (user_id),
    INDEX idx_donor_blood_group (blood_group),
    INDEX idx_donor_city (city),
    INDEX idx_donor_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table blood_requests
CREATE TABLE blood_requests (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    additional_info text DEFAULT NULL,
    blood_group enum('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    city varchar(100) NOT NULL,
    contact_phone varchar(15) NOT NULL,
    created_at datetime(6) DEFAULT NULL,
    expired_at datetime(6) NOT NULL,
    fulfilled_at datetime(6) DEFAULT NULL,
    hospital_name varchar(200) NOT NULL,
    latitude decimal(10,8) DEFAULT NULL,
    longitude decimal(11,8) DEFAULT NULL,
    patient_name varchar(100) NOT NULL,
    status enum('PENDING','MATCHING','ACTIVE','FULFILLED','EXPIRED','CANCELLED') NOT NULL,
    units_required int NOT NULL,
    updated_at datetime(6) DEFAULT NULL,
    urgency enum('NORMAL','URGENT','CRITICAL') NOT NULL,
    created_by bigint NOT NULL,
    CONSTRAINT fk_br_user FOREIGN KEY (created_by) REFERENCES users (id),
    INDEX idx_br_status (status),
    INDEX idx_br_blood_group (blood_group),
    INDEX idx_br_city (city),
    INDEX idx_br_urgency (urgency),
    INDEX idx_br_expired_at (expired_at),
    INDEX idx_br_created_at (created_at),
    INDEX idx_br_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table donor_request_responses
CREATE TABLE donor_request_responses (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    notes varchar(500) DEFAULT NULL,
    notified_at datetime(6) DEFAULT NULL,
    responded_at datetime(6) DEFAULT NULL,
    status enum('NOTIFIED','ACCEPTED','REJECTED','NO_RESPONSE','CANCELLED') NOT NULL,
    updated_at datetime(6) DEFAULT NULL,
    donor_id bigint NOT NULL,
    request_id bigint NOT NULL,
    CONSTRAINT unique_donor_request UNIQUE (donor_id, request_id),
    CONSTRAINT fk_drr_donor FOREIGN KEY (donor_id) REFERENCES donor_profiles (id),
    CONSTRAINT fk_drr_request FOREIGN KEY (request_id) REFERENCES blood_requests (id),
    INDEX idx_drr_donor (donor_id),
    INDEX idx_drr_request (request_id),
    INDEX idx_drr_status (status),
    INDEX idx_drr_notified_at (notified_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table password_reset_tokens
CREATE TABLE password_reset_tokens (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    created_at datetime(6) DEFAULT NULL,
    expires_at datetime(6) NOT NULL,
    token varchar(200) NOT NULL UNIQUE,
    used bit(1) NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_prt_token (token),
    INDEX idx_prt_user_id (user_id),
    INDEX idx_prt_expiry (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Table donation_history
CREATE TABLE donation_history (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    blood_group enum('A_POSITIVE','A_NEGATIVE','B_POSITIVE','B_NEGATIVE','AB_POSITIVE','AB_NEGATIVE','O_POSITIVE','O_NEGATIVE') NOT NULL,
    certificate_url varchar(255) DEFAULT NULL,
    created_at datetime(6) DEFAULT NULL,
    donation_date datetime(6) NOT NULL,
    hospital_name varchar(100) NOT NULL,
    is_verified bit(1) NOT NULL,
    units_donated int NOT NULL,
    updated_at datetime(6) DEFAULT NULL,
    request_id bigint NOT NULL,
    donor_id bigint NOT NULL,
    CONSTRAINT fk_dh_donor FOREIGN KEY (donor_id) REFERENCES donor_profiles (id),
    CONSTRAINT fk_dh_request FOREIGN KEY (request_id) REFERENCES blood_requests (id),
    INDEX idx_donation_donor (donor_id),
    INDEX idx_donation_request (request_id),
    INDEX idx_donation_date (donation_date),
    INDEX idx_donation_blood_group (blood_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Table audit_logs
CREATE TABLE audit_logs (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    action varchar(100) NOT NULL,
    details text DEFAULT NULL,
    performed_by varchar(150) DEFAULT NULL,
    client_ip varchar(45) DEFAULT NULL,
    timestamp datetime(6) NOT NULL,
    INDEX idx_audit_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
