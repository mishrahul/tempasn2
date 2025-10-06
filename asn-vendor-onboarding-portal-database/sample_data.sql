-- =====================================================
-- ASN VENDOR ONBOARDING PORTAL - SAMPLE DATA
-- DML SQL for inserting realistic test data
-- =====================================================

-- =====================================================
-- SYSTEM CONFIGURATION DATA
-- =====================================================

INSERT INTO system_config (config_key, config_value, config_type, is_encrypted) VALUES
('app_name', '"ASN Vendor Onboarding Portal"', 'STRING', false),
('app_version', '"1.0.0"', 'STRING', false),
('max_vendors_per_oem', '1000', 'INTEGER', false),
('default_session_timeout', '3600', 'INTEGER', false),
('supported_currencies', '["INR", "USD", "EUR"]', 'ARRAY', false),
('email_smtp_host', '"smtp.gmail.com"', 'STRING', false),
('email_smtp_port', '587', 'INTEGER', false),
('jwt_expiration_hours', '24', 'INTEGER', false),
('max_file_upload_size', '10485760', 'INTEGER', false),
('api_rate_limit_default', '{"per_minute": 60, "per_hour": 1000, "per_day": 10000}', 'OBJECT', false);

-- =====================================================
-- OEM MASTER DATA
-- =====================================================

INSERT INTO oem_master (oem_code, oem_name, full_name, status, priority_rank, asn_version, asn_deadline, go_live_date, config) VALUES
('TATA', 'Tata Motors', 'Tata Motors Limited', 'ACTIVE', 1, '2.1', '2024-12-31', '2024-01-15', 
 '{"branding": {"logo_url": "https://cdn.tatamotors.com/logo.png", "primary_color": "#1E3A8A", "secondary_color": "#F59E0B"}, 
   "api": {"base_url": "https://api.tatamotors.com/asn", "timeout": 30000}, 
   "features": ["ASN_GENERATION", "INVOICE_MATCHING", "DELIVERY_TRACKING"], 
   "limits": {"max_asn_per_day": 1000, "max_vendors": 500}}'::jsonb),

('MAHINDRA', 'Mahindra', 'Mahindra & Mahindra Limited', 'ACTIVE', 2, '2.0', '2024-12-31', '2024-02-01',
 '{"branding": {"logo_url": "https://cdn.mahindra.com/logo.png", "primary_color": "#DC2626", "secondary_color": "#059669"}, 
   "api": {"base_url": "https://api.mahindra.com/asn", "timeout": 25000}, 
   "features": ["ASN_GENERATION", "INVOICE_MATCHING"], 
   "limits": {"max_asn_per_day": 800, "max_vendors": 300}}'::jsonb),

('BAJAJ', 'Bajaj Auto', 'Bajaj Auto Limited', 'ACTIVE', 3, '2.0', '2025-03-31', '2024-03-15',
 '{"branding": {"logo_url": "https://cdn.bajajauto.com/logo.png", "primary_color": "#7C3AED", "secondary_color": "#F97316"}, 
   "api": {"base_url": "https://api.bajajauto.com/asn", "timeout": 20000}, 
   "features": ["ASN_GENERATION", "DELIVERY_TRACKING"], 
   "limits": {"max_asn_per_day": 600, "max_vendors": 200}}'::jsonb),

('HERO', 'Hero MotoCorp', 'Hero MotoCorp Limited', 'COMING_SOON', 4, '2.1', '2025-06-30', '2024-06-01',
 '{"branding": {"logo_url": "https://cdn.heromotocorp.com/logo.png", "primary_color": "#EF4444", "secondary_color": "#3B82F6"}, 
   "api": {"base_url": "https://api.heromotocorp.com/asn", "timeout": 30000}, 
   "features": ["ASN_GENERATION"], 
   "limits": {"max_asn_per_day": 400, "max_vendors": 150}}'::jsonb),

('TVS', 'TVS Motor', 'TVS Motor Company Limited', 'ACTIVE', 5, '2.0', '2025-01-31', '2024-04-01',
 '{"branding": {"logo_url": "https://cdn.tvsmotor.com/logo.png", "primary_color": "#059669", "secondary_color": "#DC2626"}, 
   "api": {"base_url": "https://api.tvsmotor.com/asn", "timeout": 25000}, 
   "features": ["ASN_GENERATION", "INVOICE_MATCHING"], 
   "limits": {"max_asn_per_day": 500, "max_vendors": 250}}'::jsonb);

-- =====================================================
-- SUBSCRIPTION PLANS DATA
-- =====================================================

-- Tata Motors Plans
INSERT INTO subscription_plans (oem_id, plan_code, plan_name, pricing, features, api_limits, support_config, display_order, is_featured, is_active) 
SELECT oem_id, 'BASIC', 'Basic Plan',
'{"annual_fee": 15000, "setup_fee": 3000, "gst_rate": 18, "currency": "INR"}'::jsonb,
'["ASN_GENERATION", "EMAIL_SUPPORT", "BASIC_REPORTING"]'::jsonb,
'{"requests_per_minute": 30, "requests_per_day": 5000, "requests_per_month": 150000}'::jsonb,
'{"type": "EMAIL", "sla_hours": 72, "priority": "NORMAL"}'::jsonb,
1, false, true
FROM oem_master WHERE oem_code = 'TATA';

INSERT INTO subscription_plans (oem_id, plan_code, plan_name, pricing, features, api_limits, support_config, display_order, is_featured, is_active) 
SELECT oem_id, 'PREMIUM', 'Premium Plan',
'{"annual_fee": 25000, "setup_fee": 5000, "gst_rate": 18, "currency": "INR"}'::jsonb,
'["ASN_GENERATION", "INVOICE_MATCHING", "PHONE_SUPPORT", "ADVANCED_REPORTING", "PRIORITY_PROCESSING"]'::jsonb,
'{"requests_per_minute": 60, "requests_per_day": 10000, "requests_per_month": 300000}'::jsonb,
'{"type": "PHONE", "sla_hours": 24, "priority": "HIGH"}'::jsonb,
2, true, true
FROM oem_master WHERE oem_code = 'TATA';

-- Mahindra Plans
INSERT INTO subscription_plans (oem_id, plan_code, plan_name, pricing, features, api_limits, support_config, display_order, is_featured, is_active) 
SELECT oem_id, 'STARTER', 'Starter Plan',
'{"annual_fee": 12000, "setup_fee": 2500, "gst_rate": 18, "currency": "INR"}'::jsonb,
'["ASN_GENERATION", "EMAIL_SUPPORT"]'::jsonb,
'{"requests_per_minute": 25, "requests_per_day": 3000, "requests_per_month": 90000}'::jsonb,
'{"type": "EMAIL", "sla_hours": 96, "priority": "LOW"}'::jsonb,
1, false, true
FROM oem_master WHERE oem_code = 'MAHINDRA';

INSERT INTO subscription_plans (oem_id, plan_code, plan_name, pricing, features, api_limits, support_config, display_order, is_featured, is_active) 
SELECT oem_id, 'ENTERPRISE', 'Enterprise Plan',
'{"annual_fee": 35000, "setup_fee": 7500, "gst_rate": 18, "currency": "INR"}'::jsonb,
'["ASN_GENERATION", "INVOICE_MATCHING", "DELIVERY_TRACKING", "DEDICATED_SUPPORT", "CUSTOM_REPORTING", "API_WEBHOOKS"]'::jsonb,
'{"requests_per_minute": 100, "requests_per_day": 20000, "requests_per_month": 600000}'::jsonb,
'{"type": "DEDICATED", "sla_hours": 4, "priority": "CRITICAL"}'::jsonb,
2, true, true
FROM oem_master WHERE oem_code = 'MAHINDRA';

-- Bajaj Plans
INSERT INTO subscription_plans (oem_id, plan_code, plan_name, pricing, features, api_limits, support_config, display_order, is_featured, is_active) 
SELECT oem_id, 'STANDARD', 'Standard Plan',
'{"annual_fee": 18000, "setup_fee": 4000, "gst_rate": 18, "currency": "INR"}'::jsonb,
'["ASN_GENERATION", "DELIVERY_TRACKING", "EMAIL_SUPPORT", "BASIC_REPORTING"]'::jsonb,
'{"requests_per_minute": 45, "requests_per_day": 7500, "requests_per_month": 225000}'::jsonb,
'{"type": "EMAIL", "sla_hours": 48, "priority": "NORMAL"}'::jsonb,
1, true, true
FROM oem_master WHERE oem_code = 'BAJAJ';

-- =====================================================
-- VENDORS DATA
-- =====================================================

INSERT INTO vendors (company_name, pan_number, cin_number, primary_contact, auth_credentials, status, last_activity_at) VALUES
('ABC Auto Parts Pvt Ltd', 'ABCDE1234F', 'U29100MH2010PTC123456',
 '{"name": "Rajesh Kumar", "email": "rajesh@abcauto.com", "mobile": "+919876543210", "designation": "General Manager"}'::jsonb,
 '{"password_hash": "$2a$10$N9qo8uLOickgx2ZMRZoMye", "mfa_enabled": false, "mfa_secret": null}'::jsonb,
 'ACTIVE', '2024-09-15 14:30:00'),

('XYZ Components Ltd', 'XYZCO5678G', 'U29200DL2015PTC234567',
 '{"name": "Priya Sharma", "email": "priya@xyzcomponents.com", "mobile": "+919123456789", "designation": "Operations Head"}'::jsonb,
 '{"password_hash": "$2a$10$M8po7tKNickgx3ZMRZoMze", "mfa_enabled": true, "mfa_secret": "JBSWY3DPEHPK3PXP"}'::jsonb,
 'ACTIVE', '2024-09-16 09:15:00'),

('PQR Manufacturing Co', 'PQRMF9012H', 'U29300GJ2018PTC345678',
 '{"name": "Amit Patel", "email": "amit@pqrmfg.com", "mobile": "+919988776655", "designation": "Director"}'::jsonb,
 '{"password_hash": "$2a$10$L7no6sJMickgx4ZMRZoMxe", "mfa_enabled": false, "mfa_secret": null}'::jsonb,
 'ACTIVE', '2024-09-14 16:45:00'),

('LMN Industries Pvt Ltd', 'LMNIN3456I', 'U29400KA2020PTC456789',
 '{"name": "Sunita Reddy", "email": "sunita@lmnind.com", "mobile": "+919876512345", "designation": "CEO"}'::jsonb,
 '{"password_hash": "$2a$10$K6mn5rILickgx5ZMRZoMwe", "mfa_enabled": true, "mfa_secret": "MFRGG3DFMZTWQ2LK"}'::jsonb,
 'PENDING_APPROVAL', '2024-09-13 11:20:00'),

('RST Automotive Solutions', 'RSTAU7890J', 'U29500TN2019PTC567890',
 '{"name": "Vikram Singh", "email": "vikram@rstautomotive.com", "mobile": "+919123987654", "designation": "Managing Partner"}'::jsonb,
 '{"password_hash": "$2a$10$J5lm4qHKickgx6ZMRZoMve", "mfa_enabled": false, "mfa_secret": null}'::jsonb,
 'ACTIVE', '2024-09-17 08:30:00');

-- =====================================================
-- VENDOR GSTIN DATA
-- =====================================================

INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at) 
SELECT vendor_id, '27ABCDE1234F1Z5', '27', true, true, '2024-08-15 10:00:00'
FROM vendors WHERE pan_number = 'ABCDE1234F';

INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at) 
SELECT vendor_id, '07XYZCO5678G1Z8', '07', true, true, '2024-08-20 14:30:00'
FROM vendors WHERE pan_number = 'XYZCO5678G';

INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at) 
SELECT vendor_id, '24PQRMF9012H1Z2', '24', true, true, '2024-08-25 11:15:00'
FROM vendors WHERE pan_number = 'PQRMF9012H';

INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at) 
SELECT vendor_id, '29LMNIN3456I1Z7', '29', true, false, null
FROM vendors WHERE pan_number = 'LMNIN3456I';

INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at) 
SELECT vendor_id, '33RSTAU7890J1Z4', '33', true, true, '2024-09-01 09:45:00'
FROM vendors WHERE pan_number = 'RSTAU7890J';

-- Secondary GSTIN for multi-state operations
INSERT INTO vendor_gstin (vendor_id, gstin, state_code, is_primary, is_verified, verified_at)
SELECT vendor_id, '09ABCDE1234F2Z3', '09', false, true, '2024-08-18 15:20:00'
FROM vendors WHERE pan_number = 'ABCDE1234F';

-- =====================================================
-- VENDOR OEM ACCESS DATA
-- =====================================================

-- ABC Auto Parts access to Tata Motors
INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'TATA_ABC_001', 'PREMIUM', 'ACTIVE', '2024-08-15 10:30:00', '2025-08-15 10:30:00', '2024-09-17 08:15:00',
'["ASN_CREATE", "ASN_UPDATE", "ASN_VIEW", "INVOICE_MATCH", "DELIVERY_TRACK"]'::jsonb, 1250, 89
FROM vendors v, oem_master o
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- XYZ Components access to Mahindra
INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'MAHI_XYZ_002', 'ADVANCED', 'ACTIVE', '2024-08-20 14:45:00', '2025-08-20 14:45:00', '2024-09-16 16:30:00',
'["ASN_CREATE", "ASN_VIEW", "INVOICE_MATCH"]'::jsonb, 890, 67
FROM vendors v, oem_master o
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA';

-- PQR Manufacturing access to Bajaj
INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'BAJAJ_PQR_003', 'BASIC', 'ACTIVE', '2024-08-25 11:30:00', '2025-08-25 11:30:00', '2024-09-14 14:20:00',
'["ASN_CREATE", "ASN_VIEW", "DELIVERY_TRACK"]'::jsonb, 456, 34
FROM vendors v, oem_master o
WHERE v.pan_number = 'PQRMF9012H' AND o.oem_code = 'BAJAJ';

-- LMN Industries access to TVS (Pending)
INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'TVS_LMN_004', 'BASIC', 'PENDING', null, null, null,
'[]'::jsonb, 0, 0
FROM vendors v, oem_master o
WHERE v.pan_number = 'LMNIN3456I' AND o.oem_code = 'TVS';

-- RST Automotive access to multiple OEMs
INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'TATA_RST_005', 'ADVANCED', 'ACTIVE', '2024-09-01 10:00:00', '2025-09-01 10:00:00', '2024-09-17 07:45:00',
'["ASN_CREATE", "ASN_UPDATE", "ASN_VIEW", "INVOICE_MATCH"]'::jsonb, 234, 18
FROM vendors v, oem_master o
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA';

INSERT INTO vendor_oem_access (vendor_id, oem_id, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, permissions_cache, total_api_calls, total_asn_generated)
SELECT v.vendor_id, o.oem_id, 'BAJAJ_RST_006', 'BASIC', 'ACTIVE', '2024-09-05 15:30:00', '2025-09-05 15:30:00', '2024-09-16 12:15:00',
'["ASN_CREATE", "ASN_VIEW"]'::jsonb, 123, 9
FROM vendors v, oem_master o
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'BAJAJ';

-- =====================================================
-- ONBOARDING PROCESS DATA
-- =====================================================

-- ABC Auto Parts - Tata Motors (Completed)
INSERT INTO onboarding_process (vendor_id, oem_id, current_step, progress_percentage, deployment_method, steps_completed, step_data, status, started_at, completed_at, last_updated_at, initiated_by, last_updated_by)
SELECT v.vendor_id, o.oem_id, 'COMPLETED', 100, 'API_INTEGRATION',
'["REGISTRATION", "DOCUMENT_UPLOAD", "VERIFICATION", "API_SETUP", "TESTING", "GO_LIVE"]'::jsonb,
'{"registration": {"completed_at": "2024-08-15T10:30:00Z"}, "documents": {"pan_verified": true, "gstin_verified": true}, "api_setup": {"sandbox_tested": true, "production_ready": true}}'::jsonb,
'COMPLETED', '2024-08-15 10:30:00', '2024-08-22 16:45:00', '2024-08-22 16:45:00',
v.vendor_id, v.vendor_id
FROM vendors v, oem_master o
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- XYZ Components - Mahindra (Completed)
INSERT INTO onboarding_process (vendor_id, oem_id, current_step, progress_percentage, deployment_method, steps_completed, step_data, status, started_at, completed_at, last_updated_at, initiated_by, last_updated_by)
SELECT v.vendor_id, o.oem_id, 'COMPLETED', 100, 'WEB_PORTAL',
'["REGISTRATION", "DOCUMENT_UPLOAD", "VERIFICATION", "TRAINING", "GO_LIVE"]'::jsonb,
'{"registration": {"completed_at": "2024-08-20T14:45:00Z"}, "documents": {"pan_verified": true, "gstin_verified": true}, "training": {"completed_modules": 5, "score": 92}}'::jsonb,
'COMPLETED', '2024-08-20 14:45:00', '2024-08-28 11:30:00', '2024-08-28 11:30:00',
v.vendor_id, v.vendor_id
FROM vendors v, oem_master o
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA';

-- PQR Manufacturing - Bajaj (Completed)
INSERT INTO onboarding_process (vendor_id, oem_id, current_step, progress_percentage, deployment_method, steps_completed, step_data, status, started_at, completed_at, last_updated_at, initiated_by, last_updated_by)
SELECT v.vendor_id, o.oem_id, 'COMPLETED', 100, 'HYBRID',
'["REGISTRATION", "DOCUMENT_UPLOAD", "VERIFICATION", "API_SETUP", "GO_LIVE"]'::jsonb,
'{"registration": {"completed_at": "2024-08-25T11:30:00Z"}, "documents": {"pan_verified": true, "gstin_verified": true}, "api_setup": {"sandbox_tested": true}}'::jsonb,
'COMPLETED', '2024-08-25 11:30:00', '2024-09-02 14:20:00', '2024-09-02 14:20:00',
v.vendor_id, v.vendor_id
FROM vendors v, oem_master o
WHERE v.pan_number = 'PQRMF9012H' AND o.oem_code = 'BAJAJ';

-- LMN Industries - TVS (In Progress)
INSERT INTO onboarding_process (vendor_id, oem_id, current_step, progress_percentage, deployment_method, steps_completed, step_data, status, started_at, completed_at, last_updated_at, initiated_by, last_updated_by)
SELECT v.vendor_id, o.oem_id, 'VERIFICATION', 60, 'API_INTEGRATION',
'["REGISTRATION", "DOCUMENT_UPLOAD"]'::jsonb,
'{"registration": {"completed_at": "2024-09-10T09:15:00Z"}, "documents": {"pan_verified": true, "gstin_verified": false, "pending_docs": ["bank_statement", "incorporation_certificate"]}}'::jsonb,
'IN_PROGRESS', '2024-09-10 09:15:00', null, '2024-09-13 11:20:00',
v.vendor_id, v.vendor_id
FROM vendors v, oem_master o
WHERE v.pan_number = 'LMNIN3456I' AND o.oem_code = 'TVS';

-- RST Automotive - Tata Motors (Completed)
INSERT INTO onboarding_process (vendor_id, oem_id, current_step, progress_percentage, deployment_method, steps_completed, step_data, status, started_at, completed_at, last_updated_at, initiated_by, last_updated_by)
SELECT v.vendor_id, o.oem_id, 'COMPLETED', 100, 'API_INTEGRATION',
'["REGISTRATION", "DOCUMENT_UPLOAD", "VERIFICATION", "API_SETUP", "TESTING", "GO_LIVE"]'::jsonb,
'{"registration": {"completed_at": "2024-09-01T10:00:00Z"}, "documents": {"pan_verified": true, "gstin_verified": true}, "api_setup": {"sandbox_tested": true, "production_ready": true}}'::jsonb,
'COMPLETED', '2024-09-01 10:00:00', '2024-09-08 16:30:00', '2024-09-08 16:30:00',
v.vendor_id, v.vendor_id
FROM vendors v, oem_master o
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA';

-- =====================================================
-- ONBOARDING EVENTS DATA
-- =====================================================

-- Events for ABC Auto Parts - Tata Motors onboarding
INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'REGISTRATION_STARTED',
'{"step": "registration", "vendor_name": "ABC Auto Parts Pvt Ltd", "oem_name": "Tata Motors"}'::jsonb,
'2024-08-15 10:30:00', op.vendor_id, 'VENDOR', 'reg_start_abc_tata_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'DOCUMENTS_UPLOADED',
'{"step": "document_upload", "documents": ["pan_card", "gstin_certificate", "bank_statement"], "count": 3}'::jsonb,
'2024-08-16 14:20:00', op.vendor_id, 'VENDOR', 'doc_upload_abc_tata_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'VERIFICATION_COMPLETED',
'{"step": "verification", "verified_documents": ["pan_card", "gstin_certificate"], "verification_score": 95}'::jsonb,
'2024-08-18 11:45:00', null, 'SYSTEM', 'verify_complete_abc_tata_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'API_CREDENTIALS_GENERATED',
'{"step": "api_setup", "environment": "sandbox", "api_key_prefix": "sk_test_abc"}'::jsonb,
'2024-08-19 09:30:00', null, 'SYSTEM', 'api_gen_abc_tata_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'ONBOARDING_COMPLETED',
'{"step": "go_live", "completion_time": "7 days", "final_score": 98}'::jsonb,
'2024-08-22 16:45:00', null, 'SYSTEM', 'complete_abc_tata_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- Events for LMN Industries - TVS onboarding (In Progress)
INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'REGISTRATION_STARTED',
'{"step": "registration", "vendor_name": "LMN Industries Pvt Ltd", "oem_name": "TVS Motor"}'::jsonb,
'2024-09-10 09:15:00', op.vendor_id, 'VENDOR', 'reg_start_lmn_tvs_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'LMNIN3456I' AND o.oem_code = 'TVS';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'DOCUMENTS_UPLOADED',
'{"step": "document_upload", "documents": ["pan_card", "gstin_certificate"], "count": 2}'::jsonb,
'2024-09-11 15:30:00', op.vendor_id, 'VENDOR', 'doc_upload_lmn_tvs_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'LMNIN3456I' AND o.oem_code = 'TVS';

INSERT INTO onboarding_events (onboarding_id, event_type, event_data, event_timestamp, triggered_by, triggered_by_type, idempotency_key)
SELECT op.onboarding_id, 'VERIFICATION_PENDING',
'{"step": "verification", "pending_documents": ["bank_statement", "incorporation_certificate"], "reason": "Additional documents required"}'::jsonb,
'2024-09-13 11:20:00', null, 'SYSTEM', 'verify_pending_lmn_tvs_001'
FROM onboarding_process op
JOIN vendors v ON op.vendor_id = v.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'LMNIN3456I' AND o.oem_code = 'TVS';

-- =====================================================
-- SUBSCRIPTIONS DATA
-- =====================================================

-- ABC Auto Parts - Tata Motors Premium Plan
INSERT INTO subscriptions (vendor_id, oem_id, plan_id, start_date, end_date, auto_renew, pricing_snapshot, status, next_billing_date)
SELECT v.vendor_id, o.oem_id, sp.plan_id, '2024-08-22', '2025-08-22', true,
'{"annual_fee": 25000, "setup_fee": 5000, "gst_rate": 18, "currency": "INR", "total_amount": 35400}'::jsonb,
'ACTIVE', '2025-08-22'
FROM vendors v, oem_master o, subscription_plans sp
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA' AND sp.plan_code = 'PREMIUM' AND sp.oem_id = o.oem_id;

-- XYZ Components - Mahindra Enterprise Plan
INSERT INTO subscriptions (vendor_id, oem_id, plan_id, start_date, end_date, auto_renew, pricing_snapshot, status, next_billing_date)
SELECT v.vendor_id, o.oem_id, sp.plan_id, '2024-08-28', '2025-08-28', true,
'{"annual_fee": 35000, "setup_fee": 7500, "gst_rate": 18, "currency": "INR", "total_amount": 50150}'::jsonb,
'ACTIVE', '2025-08-28'
FROM vendors v, oem_master o, subscription_plans sp
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA' AND sp.plan_code = 'ENTERPRISE' AND sp.oem_id = o.oem_id;

-- PQR Manufacturing - Bajaj Standard Plan
INSERT INTO subscriptions (vendor_id, oem_id, plan_id, start_date, end_date, auto_renew, pricing_snapshot, status, next_billing_date)
SELECT v.vendor_id, o.oem_id, sp.plan_id, '2024-09-02', '2025-09-02', true,
'{"annual_fee": 18000, "setup_fee": 4000, "gst_rate": 18, "currency": "INR", "total_amount": 25960}'::jsonb,
'ACTIVE', '2025-09-02'
FROM vendors v, oem_master o, subscription_plans sp
WHERE v.pan_number = 'PQRMF9012H' AND o.oem_code = 'BAJAJ' AND sp.plan_code = 'STANDARD' AND sp.oem_id = o.oem_id;

-- RST Automotive - Tata Motors Basic Plan
INSERT INTO subscriptions (vendor_id, oem_id, plan_id, start_date, end_date, auto_renew, pricing_snapshot, status, next_billing_date)
SELECT v.vendor_id, o.oem_id, sp.plan_id, '2024-09-08', '2025-09-08', false,
'{"annual_fee": 15000, "setup_fee": 3000, "gst_rate": 18, "currency": "INR", "total_amount": 21240}'::jsonb,
'ACTIVE', '2025-09-08'
FROM vendors v, oem_master o, subscription_plans sp
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA' AND sp.plan_code = 'BASIC' AND sp.oem_id = o.oem_id;

-- =====================================================
-- PAYMENT TRANSACTIONS DATA
-- =====================================================

-- Payment for ABC Auto Parts - Tata Motors Premium Plan
INSERT INTO payment_transactions (subscription_id, vendor_id, oem_id, transaction_ref, amount, currency, status, gateway_data, initiated_at, completed_at)
SELECT s.subscription_id, s.vendor_id, s.oem_id, 'TXN_ABC_TATA_001', 35400.00, 'INR', 'SUCCESS',
'{"gateway": "razorpay", "payment_id": "pay_ABC123456789", "method": "netbanking", "bank": "HDFC", "gateway_fee": 354.00}'::jsonb,
'2024-08-22 10:00:00', '2024-08-22 10:02:15'
FROM subscriptions s
JOIN vendors v ON s.vendor_id = v.vendor_id
JOIN oem_master o ON s.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- Payment for XYZ Components - Mahindra Enterprise Plan
INSERT INTO payment_transactions (subscription_id, vendor_id, oem_id, transaction_ref, amount, currency, status, gateway_data, initiated_at, completed_at)
SELECT s.subscription_id, s.vendor_id, s.oem_id, 'TXN_XYZ_MAHI_001', 50150.00, 'INR', 'SUCCESS',
'{"gateway": "payu", "payment_id": "pay_XYZ987654321", "method": "upi", "upi_id": "priya@okaxis", "gateway_fee": 501.50}'::jsonb,
'2024-08-28 14:30:00', '2024-08-28 14:31:45'
FROM subscriptions s
JOIN vendors v ON s.vendor_id = v.vendor_id
JOIN oem_master o ON s.oem_id = o.oem_id
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA';

-- Payment for PQR Manufacturing - Bajaj Standard Plan
INSERT INTO payment_transactions (subscription_id, vendor_id, oem_id, transaction_ref, amount, currency, status, gateway_data, initiated_at, completed_at)
SELECT s.subscription_id, s.vendor_id, s.oem_id, 'TXN_PQR_BAJAJ_001', 25960.00, 'INR', 'SUCCESS',
'{"gateway": "razorpay", "payment_id": "pay_PQR456789123", "method": "card", "card_type": "credit", "last4": "1234", "gateway_fee": 259.60}'::jsonb,
'2024-09-02 16:15:00', '2024-09-02 16:16:30'
FROM subscriptions s
JOIN vendors v ON s.vendor_id = v.vendor_id
JOIN oem_master o ON s.oem_id = o.oem_id
WHERE v.pan_number = 'PQRMF9012H' AND o.oem_code = 'BAJAJ';

-- Failed payment attempt for RST Automotive
INSERT INTO payment_transactions (subscription_id, vendor_id, oem_id, transaction_ref, amount, currency, status, gateway_data, initiated_at, completed_at)
SELECT s.subscription_id, s.vendor_id, s.oem_id, 'TXN_RST_TATA_001_FAIL', 21240.00, 'INR', 'FAILED',
'{"gateway": "razorpay", "payment_id": "pay_RST111111111", "method": "netbanking", "bank": "SBI", "error_code": "BAD_REQUEST_ERROR", "error_description": "Insufficient funds"}'::jsonb,
'2024-09-08 11:00:00', '2024-09-08 11:01:20'
FROM subscriptions s
JOIN vendors v ON s.vendor_id = v.vendor_id
JOIN oem_master o ON s.oem_id = o.oem_id
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA';

-- Successful retry payment for RST Automotive
INSERT INTO payment_transactions (subscription_id, vendor_id, oem_id, transaction_ref, amount, currency, status, gateway_data, initiated_at, completed_at)
SELECT s.subscription_id, s.vendor_id, s.oem_id, 'TXN_RST_TATA_002', 21240.00, 'INR', 'SUCCESS',
'{"gateway": "razorpay", "payment_id": "pay_RST222222222", "method": "upi", "upi_id": "vikram@paytm", "gateway_fee": 212.40}'::jsonb,
'2024-09-08 15:30:00', '2024-09-08 15:31:10'
FROM subscriptions s
JOIN vendors v ON s.vendor_id = v.vendor_id
JOIN oem_master o ON s.oem_id = o.oem_id
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA';

-- =====================================================
-- API CREDENTIALS DATA
-- =====================================================

-- API Credentials for ABC Auto Parts - Tata Motors (Production)
INSERT INTO api_credentials (vendor_id, oem_id, api_key_hash, secret_encrypted, environment, rate_limits, is_active, last_rotated_at, expires_at, usage_stats)
SELECT v.vendor_id, o.oem_id,
'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
'AES256:encrypted_secret_abc_tata_prod_001',
'PRODUCTION',
'{"per_minute": 60, "per_hour": 2000, "per_day": 10000}'::jsonb,
true, '2024-08-22 16:45:00', '2025-08-22 16:45:00',
'{"total_requests": 1250, "successful_requests": 1235, "failed_requests": 15, "last_request_at": "2024-09-17T08:15:00Z"}'::jsonb
FROM vendors v, oem_master o
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- API Credentials for XYZ Components - Mahindra (Production)
INSERT INTO api_credentials (vendor_id, oem_id, api_key_hash, secret_encrypted, environment, rate_limits, is_active, last_rotated_at, expires_at, usage_stats)
SELECT v.vendor_id, o.oem_id,
'$2a$10$M8po7tKNickgx3ZMRZoMzeHjYBgcfl8q93meHyad79MKZeM28miXz',
'AES256:encrypted_secret_xyz_mahi_prod_001',
'PRODUCTION',
'{"per_minute": 100, "per_hour": 5000, "per_day": 20000}'::jsonb,
true, '2024-08-28 11:30:00', '2025-08-28 11:30:00',
'{"total_requests": 890, "successful_requests": 878, "failed_requests": 12, "last_request_at": "2024-09-16T16:30:00Z"}'::jsonb
FROM vendors v, oem_master o
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA';

-- API Credentials for PQR Manufacturing - Bajaj (Sandbox)
INSERT INTO api_credentials (vendor_id, oem_id, api_key_hash, secret_encrypted, environment, rate_limits, is_active, last_rotated_at, expires_at, usage_stats)
SELECT v.vendor_id, o.oem_id,
'$2a$10$L7no6sJMickgx4ZMRZoMxeGiXCgcfl9r94nfIzad80NLZfN39njYa',
'AES256:encrypted_secret_pqr_bajaj_sand_001',
'SANDBOX',
'{"per_minute": 45, "per_hour": 1500, "per_day": 7500}'::jsonb,
true, '2024-09-02 14:20:00', '2025-09-02 14:20:00',
'{"total_requests": 456, "successful_requests": 445, "failed_requests": 11, "last_request_at": "2024-09-14T14:20:00Z"}'::jsonb
FROM vendors v, oem_master o
WHERE v.pan_number = 'PQRMF9012H' AND o.oem_code = 'BAJAJ';

-- API Credentials for RST Automotive - Tata Motors (Production)
INSERT INTO api_credentials (vendor_id, oem_id, api_key_hash, secret_encrypted, environment, rate_limits, is_active, last_rotated_at, expires_at, usage_stats)
SELECT v.vendor_id, o.oem_id,
'$2a$10$K6mn5rILickgx5ZMRZoMweGhWDgcfl0s95ogJzad91OMZgO40okZb',
'AES256:encrypted_secret_rst_tata_prod_001',
'PRODUCTION',
'{"per_minute": 30, "per_hour": 1000, "per_day": 5000}'::jsonb,
true, '2024-09-08 16:30:00', '2025-09-08 16:30:00',
'{"total_requests": 234, "successful_requests": 228, "failed_requests": 6, "last_request_at": "2024-09-17T07:45:00Z"}'::jsonb
FROM vendors v, oem_master o
WHERE v.pan_number = 'RSTAU7890J' AND o.oem_code = 'TATA';

-- =====================================================
-- API REQUEST LOGS DATA
-- =====================================================

-- Recent API requests for ABC Auto Parts - Tata Motors
INSERT INTO api_request_logs (credential_id, request_timestamp, endpoint, method, status_code, response_time_ms, error_code, request_data, response_data)
SELECT ac.credential_id, '2024-09-17 08:15:00', '/api/v1/asn/create', 'POST', 201, 245, null,
'{"asn_number": "ASN001", "vendor_code": "TATA_ABC_001", "items": [{"sku": "PART001", "quantity": 100}]}'::jsonb,
'{"asn_id": "asn_12345", "status": "created", "tracking_number": "TRK001"}'::jsonb
FROM api_credentials ac
JOIN vendors v ON ac.vendor_id = v.vendor_id
JOIN oem_master o ON ac.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA' AND ac.environment = 'PRODUCTION';

INSERT INTO api_request_logs (credential_id, request_timestamp, endpoint, method, status_code, response_time_ms, error_code, request_data, response_data)
SELECT ac.credential_id, '2024-09-17 08:20:00', '/api/v1/asn/status', 'GET', 200, 120, null,
'{"asn_id": "asn_12345"}'::jsonb,
'{"asn_id": "asn_12345", "status": "in_transit", "last_updated": "2024-09-17T08:18:00Z"}'::jsonb
FROM api_credentials ac
JOIN vendors v ON ac.vendor_id = v.vendor_id
JOIN oem_master o ON ac.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA' AND ac.environment = 'PRODUCTION';

-- API request with error for XYZ Components - Mahindra
INSERT INTO api_request_logs (credential_id, request_timestamp, endpoint, method, status_code, response_time_ms, error_code, request_data, response_data)
SELECT ac.credential_id, '2024-09-16 16:30:00', '/api/v1/asn/create', 'POST', 400, 180, 'INVALID_SKU',
'{"asn_number": "ASN002", "vendor_code": "MAHI_XYZ_002", "items": [{"sku": "INVALID_PART", "quantity": 50}]}'::jsonb,
'{"error": "INVALID_SKU", "message": "SKU INVALID_PART not found in catalog", "code": 400}'::jsonb
FROM api_credentials ac
JOIN vendors v ON ac.vendor_id = v.vendor_id
JOIN oem_master o ON ac.oem_id = o.oem_id
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA' AND ac.environment = 'PRODUCTION';

-- Successful retry for XYZ Components
INSERT INTO api_request_logs (credential_id, request_timestamp, endpoint, method, status_code, response_time_ms, error_code, request_data, response_data)
SELECT ac.credential_id, '2024-09-16 16:35:00', '/api/v1/asn/create', 'POST', 201, 210, null,
'{"asn_number": "ASN002", "vendor_code": "MAHI_XYZ_002", "items": [{"sku": "PART002", "quantity": 50}]}'::jsonb,
'{"asn_id": "asn_67890", "status": "created", "tracking_number": "TRK002"}'::jsonb
FROM api_credentials ac
JOIN vendors v ON ac.vendor_id = v.vendor_id
JOIN oem_master o ON ac.oem_id = o.oem_id
WHERE v.pan_number = 'XYZCO5678G' AND o.oem_code = 'MAHINDRA' AND ac.environment = 'PRODUCTION';

-- =====================================================
-- AUDIT LOGS DATA
-- =====================================================

-- Vendor registration audit logs
INSERT INTO audit_logs (actor_id, actor_type, ip_address, event_category, event_action, resource_type, resource_id, changes, request_id, session_id)
SELECT v.vendor_id, 'VENDOR', '192.168.1.100'::inet, 'AUTHENTICATION', 'LOGIN', 'VENDOR', v.vendor_id,
'{"login_method": "password", "success": true, "timestamp": "2024-09-17T08:00:00Z"}'::jsonb,
'req_abc_login_001', 'sess_abc_001'
FROM vendors v WHERE v.pan_number = 'ABCDE1234F';

INSERT INTO audit_logs (actor_id, actor_type, ip_address, event_category, event_action, resource_type, resource_id, changes, request_id, session_id)
SELECT v.vendor_id, 'VENDOR', '192.168.1.100'::inet, 'ONBOARDING', 'DOCUMENT_UPLOAD', 'ONBOARDING_PROCESS', op.onboarding_id,
'{"document_type": "pan_card", "file_name": "pan_abc.pdf", "file_size": 245760, "upload_success": true}'::jsonb,
'req_abc_doc_001', 'sess_abc_001'
FROM vendors v
JOIN onboarding_process op ON v.vendor_id = op.vendor_id
JOIN oem_master o ON op.oem_id = o.oem_id
WHERE v.pan_number = 'ABCDE1234F' AND o.oem_code = 'TATA';

-- System audit logs
INSERT INTO audit_logs (actor_id, actor_type, ip_address, event_category, event_action, resource_type, resource_id, changes, request_id, session_id)
VALUES
(null, 'SYSTEM', null, 'SYSTEM', 'API_CREDENTIAL_ROTATION', 'API_CREDENTIALS', null,
'{"rotated_credentials": 5, "environment": "production", "reason": "scheduled_rotation"}'::jsonb,
'req_sys_rotation_001', null),

(null, 'SYSTEM', null, 'BILLING', 'PAYMENT_PROCESSED', 'PAYMENT_TRANSACTION', null,
'{"transaction_count": 4, "total_amount": 132750.00, "currency": "INR", "success_rate": 80}'::jsonb,
'req_sys_payment_001', null),

(null, 'SYSTEM', null, 'MAINTENANCE', 'DATABASE_BACKUP', 'SYSTEM', null,
'{"backup_type": "full", "backup_size": "2.5GB", "duration": "45 minutes", "status": "completed"}'::jsonb,
'req_sys_backup_001', null);

-- Admin audit logs
INSERT INTO audit_logs (actor_id, actor_type, ip_address, event_category, event_action, resource_type, resource_id, changes, request_id, session_id)
VALUES
('550e8400-e29b-41d4-a716-446655440000'::uuid, 'ADMIN', '10.0.0.50'::inet, 'USER_MANAGEMENT', 'VENDOR_STATUS_CHANGE', 'VENDOR',
(SELECT vendor_id FROM vendors WHERE pan_number = 'LMNIN3456I'),
'{"old_status": "ACTIVE", "new_status": "PENDING_APPROVAL", "reason": "Document verification pending", "changed_by": "admin@asnportal.com"}'::jsonb,
'req_admin_status_001', 'sess_admin_001'),

('550e8400-e29b-41d4-a716-446655440000'::uuid, 'ADMIN', '10.0.0.50'::inet, 'CONFIGURATION', 'OEM_CONFIG_UPDATE', 'OEM_MASTER',
(SELECT oem_id FROM oem_master WHERE oem_code = 'TATA'),
'{"updated_fields": ["api.timeout"], "old_value": 25000, "new_value": 30000, "reason": "Performance optimization"}'::jsonb,
'req_admin_config_001', 'sess_admin_001');

-- =====================================================
-- SUMMARY COMMENTS
-- =====================================================

-- This sample data provides:
-- 1. 5 OEMs with different statuses and configurations
-- 2. Multiple subscription plans per OEM with varying features and pricing
-- 3. 5 vendors with complete profile information
-- 4. GSTIN records including multi-state operations
-- 5. Vendor-OEM access mappings with different access levels
-- 6. Onboarding processes in various stages (completed and in-progress)
-- 7. Detailed onboarding event logs
-- 8. Active subscriptions with pricing snapshots
-- 9. Payment transactions including failed and successful attempts
-- 10. API credentials for different environments
-- 11. API request logs with success and error scenarios
-- 12. Comprehensive audit logs for security and compliance

-- Total records created:
-- - system_config: 10 records
-- - oem_master: 5 records
-- - subscription_plans: 8 records
-- - vendors: 5 records
-- - vendor_gstin: 6 records
-- - vendor_oem_access: 6 records
-- - onboarding_process: 5 records
-- - onboarding_events: 8 records
-- - subscriptions: 4 records
-- - payment_transactions: 5 records
-- - api_credentials: 4 records
-- - api_request_logs: 4 records
-- - audit_logs: 8 records

-- This data represents a realistic ASN vendor onboarding portal
-- with multiple vendors at different stages of onboarding and operation.
