-- V3: Add donor notification consent preferences (opt-in / opt-out)
ALTER TABLE donor_profiles
    ADD COLUMN sms_alerts_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN email_alerts_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN consent_updated_at TIMESTAMP NULL;
