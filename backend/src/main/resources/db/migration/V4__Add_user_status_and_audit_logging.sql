-- =====================================================
-- Phase 3: User Status Management & Audit Logging
-- =====================================================
-- 
-- INTERVIEW TALKING POINTS:
-- 1. User lifecycle management (active, locked, email verification)
-- 2. Audit trail for compliance (GDPR, SOC2, ISO 27001)
-- 3. Security best practices (account locking after failed attempts)
-- 4. Email verification prevents fake accounts
--
-- DATABASE DESIGN PRINCIPLES:
-- - Separate audit table (don't clutter user table)
-- - Indexed timestamp for fast audit queries
-- - Action type enum for consistency
-- - User reference with FK constraint
-- =====================================================

-- Add user status columns to users table
-- PRODUCTION REQUIREMENT: Track user account state
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT true;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS account_locked BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_sent_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Create audit_logs table for compliance and security
-- INTERVIEW: "Audit logging is required for SOC2 compliance"
-- "We track WHO did WHAT and WHEN for security investigations"
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    performed_by UUID REFERENCES users(id) ON DELETE SET NULL, -- Admin who performed action
    action VARCHAR(100) NOT NULL,  -- e.g., 'USER_CREATED', 'USER_ACTIVATED', 'PASSWORD_CHANGED'
    entity_type VARCHAR(50) NOT NULL,  -- e.g., 'USER', 'ORDER', 'PRODUCT'
    entity_id UUID,  -- ID of the affected entity
    details TEXT,  -- JSON details about the action
    ip_address VARCHAR(45),  -- IPv4 or IPv6
    user_agent TEXT,  -- Browser/device info
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for fast audit queries
-- PRODUCTION: Audit tables can get huge, need efficient queries
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_by ON audit_logs(performed_by);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);

-- Create index on users for faster status queries
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);
CREATE INDEX IF NOT EXISTS idx_users_account_locked ON users(account_locked);

-- Update existing admin user to verified
-- INTERVIEW: "Migration handles existing data properly"
UPDATE users SET 
    email_verified = true,
    active = true,
    account_locked = false,
    updated_at = CURRENT_TIMESTAMP
WHERE email = 'admin@danceylone.com';

-- Insert initial audit log for existing admin
-- INTERVIEW: "System-generated audit entries for data integrity"
INSERT INTO audit_logs (
    user_id, 
    performed_by, 
    action, 
    entity_type, 
    entity_id, 
    details,
    created_at
)
SELECT 
    id,
    id,
    'USER_VERIFIED',
    'USER',
    id,
    '{"reason": "Initial migration - admin account verified", "automated": true}',
    CURRENT_TIMESTAMP
FROM users 
WHERE email = 'admin@danceylone.com';

-- =====================================================
-- COMMENTS FOR INTERVIEW:
-- =====================================================
-- 
-- Q: Why separate audit_logs table instead of user_history?
-- A: "Audit logs track ALL entities (users, orders, products).
--     Single table makes compliance reporting easier."
--
-- Q: Why UUID instead of auto-increment?
-- A: "UUIDs prevent ID enumeration attacks and work in 
--     distributed systems without coordination."
--
-- Q: Why index created_at DESC?
-- A: "Most audit queries look for recent events first.
--     DESC index optimizes 'ORDER BY created_at DESC' queries."
--
-- Q: Why store IP address and user agent?
-- A: "Security investigations need to know WHERE action occurred.
--     Helps detect compromised accounts or unauthorized access."
--
-- Q: What's the locked_until column for?
-- A: "Temporary account locks (e.g., 30 min after 5 failed logins).
--     Auto-unlock when timestamp passes without manual intervention."
-- =====================================================
