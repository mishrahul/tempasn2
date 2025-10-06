-- =====================================================
-- ASN Vendor Onboarding Portal - Sample Data v1.0
-- =====================================================
-- This file contains comprehensive sample data for testing and development
-- Generated based on simplified_asn_schema_v1.sql
-- All UUIDs use only valid hexadecimal characters (0-9, a-f)
-- =====================================================

-- Set schema context
SET search_path TO sch_asn_vendor_onboarding_portal;

-- =====================================================
-- 1. SYSTEM CONFIGURATION DATA
-- =====================================================

-- System configuration entries
INSERT INTO system_config (config_id, company_code, config_type, config_key, config_value, is_encrypted, created_at, updated_at, created_by, updated_by) VALUES
('550e8400-e29b-41d4-a716-446655440001', 1001, 'SYSTEM', 'max_vendors_per_oem', '{"value": 1000, "description": "Maximum vendors allowed per OEM"}', false, NOW(), NOW(), NULL, NULL),
('550e8400-e29b-41d4-a716-446655440002', 1001, 'SYSTEM', 'default_session_timeout', '{"value": 3600, "description": "Default session timeout in seconds"}', false, NOW(), NOW(), NULL, NULL),
('550e8400-e29b-41d4-a716-446655440003', 1001, 'SYSTEM', 'api_rate_limit_default', '{"requests_per_minute": 100, "burst_limit": 200}', false, NOW(), NOW(), NULL, NULL),
('550e8400-e29b-41d4-a716-446655440004', 1001, 'SECURITY', 'jwt_expiration', '{"value": 86400, "description": "JWT token expiration in seconds"}', false, NOW(), NOW(), NULL, NULL),
('550e8400-e29b-41d4-a716-446655440005', 1001, 'EMAIL', 'smtp_config', '{"host": "smtp.gmail.com", "port": 587, "username": "noreply@asnportal.com"}', true, NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 2. OEM MASTER DATA
-- =====================================================

-- Major OEMs with different configurations
INSERT INTO oem_master (oem_id, company_code, oem_code, oem_name, full_name, asn_version, status, priority_rank, asn_deadline, go_live_date, config, created_at, updated_at, created_by, updated_by) VALUES
-- Maruti Suzuki
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'MSIL', 'Maruti Suzuki', 'Maruti Suzuki India Limited', 'ASN_V2.1', 'ACTIVE', 1, '2024-12-31', '2024-01-15', 
'{"api_endpoint": "https://api.marutisuzuki.com/asn", "authentication": {"type": "oauth2", "client_id": "msil_client"}, "data_format": "JSON", "mandatory_fields": ["invoice_number", "delivery_date", "vehicle_vin"], "business_rules": {"max_delivery_days": 30, "require_advance_notice": true}, "contact": {"technical": "tech@marutisuzuki.com", "business": "business@marutisuzuki.com"}}', 
NOW(), NOW(), NULL, NULL),

-- Tata Motors
('b2c3d4e5-f6a7-8901-bcde-f23456789012', 1001, 'TATA', 'Tata Motors', 'Tata Motors Limited', 'ASN_V2.0', 'ACTIVE', 2, '2024-11-30', '2024-02-01', 
'{"api_endpoint": "https://supplier.tatamotors.com/asn", "authentication": {"type": "api_key", "header": "X-API-Key"}, "data_format": "XML", "mandatory_fields": ["po_number", "part_number", "quantity"], "business_rules": {"max_delivery_days": 45, "require_quality_cert": true}, "contact": {"technical": "it-support@tatamotors.com", "business": "procurement@tatamotors.com"}}', 
NOW(), NOW(), NULL, NULL),

-- Mahindra & Mahindra
('c3d4e5f6-a7b8-9012-cdef-345678901234', 1001, 'MM', 'Mahindra', 'Mahindra & Mahindra Limited', 'ASN_V1.8', 'ACTIVE', 3, '2025-01-31', '2024-03-15', 
'{"api_endpoint": "https://vendors.mahindra.com/asn", "authentication": {"type": "bearer_token"}, "data_format": "JSON", "mandatory_fields": ["material_code", "batch_number", "expiry_date"], "business_rules": {"max_delivery_days": 21, "require_material_cert": true}, "contact": {"technical": "vendor-tech@mahindra.com", "business": "vendor-business@mahindra.com"}}', 
NOW(), NOW(), NULL, NULL),

-- Hero MotoCorp
('d4e5f6a7-b8c9-0123-defa-456789012345', 1001, 'HERO', 'Hero MotoCorp', 'Hero MotoCorp Limited', 'ASN_V2.2', 'ACTIVE', 4, '2024-10-31', '2024-04-01', 
'{"api_endpoint": "https://portal.heromotocorp.com/asn", "authentication": {"type": "oauth2", "client_id": "hero_vendor"}, "data_format": "JSON", "mandatory_fields": ["delivery_note", "transport_details", "invoice_value"], "business_rules": {"max_delivery_days": 15, "require_transport_insurance": true}, "contact": {"technical": "vendor-support@heromotocorp.com", "business": "supply-chain@heromotocorp.com"}}', 
NOW(), NOW(), NULL, NULL),

-- Bajaj Auto (Coming Soon)
('e5f6a7b8-c9d0-1234-efab-567890123456', 1001, 'BAJAJ', 'Bajaj Auto', 'Bajaj Auto Limited', 'ASN_V2.0', 'COMING_SOON', 5, '2025-03-31', '2025-01-01', 
'{"api_endpoint": "https://suppliers.bajajauto.com/asn", "authentication": {"type": "api_key"}, "data_format": "JSON", "mandatory_fields": ["part_serial", "quality_report"], "business_rules": {"max_delivery_days": 20, "require_pre_delivery_inspection": true}, "contact": {"technical": "tech-team@bajajauto.com", "business": "vendor-relations@bajajauto.com"}}', 
NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 3. VENDOR DATA
-- =====================================================

-- Sample vendors with realistic data
INSERT INTO vendors (vendor_id, company_code, pan_number, cin_number, company_name, status, auth_credentials, primary_contact, last_activity_at, created_at, updated_at, created_by, updated_by) VALUES
-- Auto Parts Manufacturer
('f6a7b8c9-d0e1-2345-fabc-678901234567', 1001, 'ABCDE1234F', 'U29100DL2010PTC123456', 'ABC Auto Parts Pvt Ltd', 'ACTIVE', 
'{"email": "admin@abcautoparts.com", "password_hash": "$2a$10$N9qo8uLOickgx2ZMRZoMye", "two_factor_enabled": true, "last_login": "2024-09-15T10:30:00Z", "login_attempts": 0}', 
'{"name": "Rajesh Kumar", "email": "rajesh.kumar@abcautoparts.com", "phone": "+91-9876543210", "designation": "General Manager - IT", "address": {"street": "Plot 123, Industrial Area", "city": "Gurgaon", "state": "Haryana", "pincode": "122001", "country": "India"}}', 
'2024-09-15 10:30:00', NOW(), NOW(), NULL, NULL),

-- Electronics Component Supplier
('a7b8c9d0-e1f2-3456-abcd-789012345678', 1001, 'FGHIJ5678K', 'U31100MH2015PTC234567', 'TechnoElectronics Solutions Ltd', 'ACTIVE', 
'{"email": "it@technoelectronics.com", "password_hash": "$2a$10$M8po7tKNjdhfx1YLQYnLxe", "two_factor_enabled": true, "last_login": "2024-09-14T15:45:00Z", "login_attempts": 0}', 
'{"name": "Priya Sharma", "email": "priya.sharma@technoelectronics.com", "phone": "+91-9123456789", "designation": "Head - Supply Chain", "address": {"street": "Electronic City, Phase 2", "city": "Bangalore", "state": "Karnataka", "pincode": "560100", "country": "India"}}', 
'2024-09-14 15:45:00', NOW(), NOW(), NULL, NULL),

-- Steel & Metal Works
('b8c9d0e1-f2a3-4567-bcde-890123456789', 1001, 'KLMNO9012P', 'U27100TN2012PTC345678', 'SteelCraft Industries Pvt Ltd', 'ACTIVE', 
'{"email": "operations@steelcraft.com", "password_hash": "$2a$10$L7no6sJMicgfw0XKPXmKwd", "two_factor_enabled": false, "last_login": "2024-09-13T09:15:00Z", "login_attempts": 0}', 
'{"name": "Suresh Reddy", "email": "suresh.reddy@steelcraft.com", "phone": "+91-9876512345", "designation": "Production Manager", "address": {"street": "Industrial Estate, Sector 5", "city": "Chennai", "state": "Tamil Nadu", "pincode": "600032", "country": "India"}}', 
'2024-09-13 09:15:00', NOW(), NOW(), NULL, NULL),

-- Rubber & Plastic Components
('c9d0e1f2-a3b4-5678-cdef-901234567890', 1001, 'PQRST3456U', 'U25200GJ2018PTC456789', 'FlexiRubber Components Ltd', 'PENDING_APPROVAL', 
'{"email": "admin@flexirubber.com", "password_hash": "$2a$10$K6mn5rILhbfev9WJOWlJvc", "two_factor_enabled": true, "last_login": "2024-09-12T14:20:00Z", "login_attempts": 0}', 
'{"name": "Amit Patel", "email": "amit.patel@flexirubber.com", "phone": "+91-9988776655", "designation": "Business Development Manager", "address": {"street": "GIDC Industrial Area", "city": "Ahmedabad", "state": "Gujarat", "pincode": "382010", "country": "India"}}', 
'2024-09-12 14:20:00', NOW(), NOW(), NULL, NULL),

-- Textile & Upholstery
('d0e1f2a3-b4c5-6789-defa-012345678901', 1001, 'UVWXY7890Z', 'U17100PB2020PTC567890', 'Premium Textiles & Upholstery', 'ACTIVE', 
'{"email": "contact@premiumtextiles.com", "password_hash": "$2a$10$J5lm4qHKgaedw8VIOVkIub", "two_factor_enabled": true, "last_login": "2024-09-11T11:00:00Z", "login_attempts": 0}', 
'{"name": "Simran Kaur", "email": "simran.kaur@premiumtextiles.com", "phone": "+91-9876598765", "designation": "Export Manager", "address": {"street": "Textile Hub, Phase 3", "city": "Ludhiana", "state": "Punjab", "pincode": "141001", "country": "India"}}', 
'2024-09-11 11:00:00', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 4. VENDOR GSTIN DATA
-- =====================================================

-- GSTIN records for vendors
INSERT INTO vendor_gstin (gstin_id, vendor_id, company_code, gstin, state_code, is_primary, is_verified, verified_at, created_at, updated_at, created_by, updated_by) VALUES
-- ABC Auto Parts - Haryana
('e1f2a3b4-c5d6-7890-efab-123456789012', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 1001, '06ABCDE1234F1Z5', '06', true, true, '2024-01-20 10:00:00', NOW(), NOW(), NULL, NULL),

-- TechnoElectronics - Karnataka
('f2a3b4c5-d6e7-8901-fabc-234567890123', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 1001, '29FGHIJ5678K1Z8', '29', true, true, '2024-02-15 14:30:00', NOW(), NOW(), NULL, NULL),

-- SteelCraft - Tamil Nadu
('a3b4c5d6-e7f8-9012-abcd-345678901234', 'b8c9d0e1-f2a3-4567-bcde-890123456789', 1001, '33KLMNO9012P1Z2', '33', true, true, '2024-03-10 09:45:00', NOW(), NOW(), NULL, NULL),

-- FlexiRubber - Gujarat
('b4c5d6e7-f8a9-0123-bcde-456789012345', 'c9d0e1f2-a3b4-5678-cdef-901234567890', 1001, '24PQRST3456U1Z7', '24', true, false, NULL, NOW(), NOW(), NULL, NULL),

-- Premium Textiles - Punjab
('c5d6e7f8-a9b0-1234-cdef-567890123456', 'd0e1f2a3-b4c5-6789-defa-012345678901', 1001, '03UVWXY7890Z1Z4', '03', true, true, '2024-04-05 16:15:00', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 5. VENDOR OEM ACCESS DATA
-- =====================================================

-- Vendor access permissions for different OEMs
INSERT INTO vendor_oem_access (access_id, vendor_id, oem_id, company_code, vendor_code, access_level, access_status, granted_at, expires_at, last_accessed_at, total_api_calls, total_asn_generated, permissions_cache, created_at, updated_at, created_by, updated_by) VALUES
-- ABC Auto Parts with Maruti Suzuki
('d6e7f8a9-b0c1-2345-defa-678901234567', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'MSIL_ABC_001', 'PREMIUM', 'ACTIVE', '2024-01-15 10:00:00', '2025-01-15 10:00:00', '2024-09-15 08:30:00', 1250, 89,
'{"asn_create": true, "asn_update": true, "asn_delete": false, "api_access": true, "bulk_upload": true, "reporting": true, "analytics": true}', NOW(), NOW(), NULL, NULL),

-- ABC Auto Parts with Tata Motors
('e7f8a9b0-c1d2-3456-efab-789012345678', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 1001, 'TATA_ABC_002', 'ADVANCED', 'ACTIVE', '2024-02-01 14:00:00', '2025-02-01 14:00:00', '2024-09-14 16:45:00', 890, 67,
'{"asn_create": true, "asn_update": true, "asn_delete": false, "api_access": true, "bulk_upload": false, "reporting": true, "analytics": false}', NOW(), NOW(), NULL, NULL),

-- TechnoElectronics with Maruti Suzuki
('f8a9b0c1-d2e3-4567-fabc-890123456789', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'MSIL_TECH_003', 'ADVANCED', 'ACTIVE', '2024-01-20 09:30:00', '2025-01-20 09:30:00', '2024-09-13 12:15:00', 756, 45,
'{"asn_create": true, "asn_update": true, "asn_delete": false, "api_access": true, "bulk_upload": false, "reporting": true, "analytics": false}', NOW(), NOW(), NULL, NULL),

-- TechnoElectronics with Hero MotoCorp
('a9b0c1d2-e3f4-5678-abcd-901234567890', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 'd4e5f6a7-b8c9-0123-defa-456789012345', 1001, 'HERO_TECH_004', 'PREMIUM', 'ACTIVE', '2024-04-01 11:00:00', '2025-04-01 11:00:00', '2024-09-12 14:20:00', 1100, 78,
'{"asn_create": true, "asn_update": true, "asn_delete": true, "api_access": true, "bulk_upload": true, "reporting": true, "analytics": true}', NOW(), NOW(), NULL, NULL),

-- SteelCraft with Tata Motors
('b0c1d2e3-f4a5-6789-bcde-012345678901', 'b8c9d0e1-f2a3-4567-bcde-890123456789', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 1001, 'TATA_STEEL_005', 'BASIC', 'ACTIVE', '2024-02-15 13:45:00', '2025-02-15 13:45:00', '2024-09-11 10:30:00', 445, 32,
'{"asn_create": true, "asn_update": false, "asn_delete": false, "api_access": true, "bulk_upload": false, "reporting": false, "analytics": false}', NOW(), NOW(), NULL, NULL),

-- SteelCraft with Mahindra
('c1d2e3f4-a5b6-7890-cdef-123456789012', 'b8c9d0e1-f2a3-4567-bcde-890123456789', 'c3d4e5f6-a7b8-9012-cdef-345678901234', 1001, 'MM_STEEL_006', 'ADVANCED', 'ACTIVE', '2024-03-15 15:20:00', '2025-03-15 15:20:00', '2024-09-10 09:45:00', 678, 51,
'{"asn_create": true, "asn_update": true, "asn_delete": false, "api_access": true, "bulk_upload": false, "reporting": true, "analytics": false}', NOW(), NOW(), NULL, NULL),

-- FlexiRubber with Mahindra (Pending)
('d2e3f4a5-b6c7-8901-defa-234567890123', 'c9d0e1f2-a3b4-5678-cdef-901234567890', 'c3d4e5f6-a7b8-9012-cdef-345678901234', 1001, 'MM_FLEXI_007', 'BASIC', 'PENDING', '2024-09-01 10:00:00', '2025-09-01 10:00:00', NULL, 0, 0,
'{"asn_create": false, "asn_update": false, "asn_delete": false, "api_access": false, "bulk_upload": false, "reporting": false, "analytics": false}', NOW(), NOW(), NULL, NULL),

-- Premium Textiles with Hero MotoCorp
('e3f4a5b6-c7d8-9012-efab-345678901234', 'd0e1f2a3-b4c5-6789-defa-012345678901', 'd4e5f6a7-b8c9-0123-defa-456789012345', 1001, 'HERO_PREM_008', 'ADVANCED', 'ACTIVE', '2024-04-10 12:30:00', '2025-04-10 12:30:00', '2024-09-09 15:10:00', 567, 41,
'{"asn_create": true, "asn_update": true, "asn_delete": false, "api_access": true, "bulk_upload": false, "reporting": true, "analytics": false}', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 6. SUBSCRIPTION PLANS DATA
-- =====================================================

-- Subscription plans for each OEM
INSERT INTO subscription_plans (plan_id, oem_id, company_code, plan_code, plan_name, is_active, is_featured, display_order, api_limits, features, pricing, support_config, created_at, updated_at, created_by, updated_by) VALUES
-- Maruti Suzuki Plans
('f4a5b6c7-d8e9-0123-fabc-456789012345', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'MSIL_BASIC', 'Basic Plan', true, false, 1,
'{"requests_per_day": 1000, "burst_limit": 100, "concurrent_connections": 5}',
'{"asn_creation": true, "api_access": true, "email_support": true, "sla_hours": 48}',
'{"monthly": 5000, "yearly": 50000, "currency": "INR", "setup_fee": 1000}',
'{"email": "support@marutisuzuki.com", "phone": "+91-124-4604000", "business_hours": "9AM-6PM IST"}', NOW(), NOW(), NULL, NULL),

('a5b6c7d8-e9f0-1234-abcd-567890123456', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'MSIL_PREMIUM', 'Premium Plan', true, true, 2,
'{"requests_per_day": 10000, "burst_limit": 500, "concurrent_connections": 20}',
'{"asn_creation": true, "api_access": true, "bulk_upload": true, "analytics": true, "priority_support": true, "sla_hours": 24}',
'{"monthly": 15000, "yearly": 150000, "currency": "INR", "setup_fee": 2000}',
'{"email": "premium-support@marutisuzuki.com", "phone": "+91-124-4604001", "business_hours": "24x7"}', NOW(), NOW(), NULL, NULL),

-- Tata Motors Plans
('b6c7d8e9-f0a1-2345-bcde-678901234567', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 1001, 'TATA_STANDARD', 'Standard Plan', true, false, 1,
'{"requests_per_day": 2000, "burst_limit": 200, "concurrent_connections": 10}',
'{"asn_creation": true, "api_access": true, "xml_support": true, "email_support": true, "sla_hours": 36}',
'{"monthly": 8000, "yearly": 80000, "currency": "INR", "setup_fee": 1500}',
'{"email": "vendor-support@tatamotors.com", "phone": "+91-22-6665-8282", "business_hours": "9AM-7PM IST"}', NOW(), NOW(), NULL, NULL),

('c7d8e9f0-a1b2-3456-cdef-789012345678', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 1001, 'TATA_ENTERPRISE', 'Enterprise Plan', true, true, 2,
'{"requests_per_day": 25000, "burst_limit": 1000, "concurrent_connections": 50}',
'{"asn_creation": true, "api_access": true, "xml_support": true, "bulk_upload": true, "analytics": true, "dedicated_support": true, "sla_hours": 12}',
'{"monthly": 25000, "yearly": 250000, "currency": "INR", "setup_fee": 5000}',
'{"email": "enterprise-support@tatamotors.com", "phone": "+91-22-6665-8283", "business_hours": "24x7"}', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 7. SUBSCRIPTIONS DATA
-- =====================================================

-- Active subscriptions for vendors
INSERT INTO subscriptions (subscription_id, vendor_id, oem_id, plan_id, company_code, status, start_date, end_date, next_billing_date, auto_renew, pricing_snapshot, created_at, updated_at, created_by, updated_by) VALUES
-- ABC Auto Parts subscriptions
('d8e9f0a1-b2c3-4567-defa-890123456789', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'a5b6c7d8-e9f0-1234-abcd-567890123456', 1001, 'ACTIVE', '2024-01-15', '2025-01-15', '2024-10-15', true,
'{"plan_name": "Premium Plan", "monthly_cost": 15000, "yearly_cost": 150000, "currency": "INR", "billing_cycle": "monthly", "discount_applied": 0}', NOW(), NOW(), NULL, NULL),

('e9f0a1b2-c3d4-5678-efab-901234567890', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 'b6c7d8e9-f0a1-2345-bcde-678901234567', 1001, 'ACTIVE', '2024-02-01', '2025-02-01', '2024-10-01', true,
'{"plan_name": "Standard Plan", "monthly_cost": 8000, "yearly_cost": 80000, "currency": "INR", "billing_cycle": "monthly", "discount_applied": 0}', NOW(), NOW(), NULL, NULL),

-- TechnoElectronics subscriptions
('f0a1b2c3-d4e5-6789-fabc-012345678901', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'b6c7d8e9-f0a1-2345-bcde-678901234567', 1001, 'ACTIVE', '2024-01-20', '2025-01-20', '2024-10-20', false,
'{"plan_name": "Standard Plan", "monthly_cost": 8000, "yearly_cost": 80000, "currency": "INR", "billing_cycle": "yearly", "discount_applied": 10}', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 8. PAYMENT TRANSACTIONS DATA
-- =====================================================

-- Payment transactions for subscriptions
INSERT INTO payment_transactions (transaction_id, vendor_id, oem_id, subscription_id, transaction_ref, amount, currency, status, initiated_at, completed_at, gateway_data) VALUES
-- ABC Auto Parts payments
('a1b2c3d4-e5f6-7890-abcd-123456789012', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'd8e9f0a1-b2c3-4567-defa-890123456789', 'TXN_ABC_MSIL_001', 15000.00, 'INR', 'SUCCESS', '2024-01-15 10:30:00', '2024-01-15 10:32:15',
'{"gateway": "Razorpay", "gateway_txn_id": "pay_ABC123456789", "payment_method": "UPI", "upi_id": "rajesh@paytm", "gateway_fee": 354.00, "net_amount": 14646.00}'),

('b2c3d4e5-f6a7-8901-bcde-234567890123', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'b2c3d4e5-f6a7-8901-bcde-f23456789012', 'e9f0a1b2-c3d4-5678-efab-901234567890', 'TXN_ABC_TATA_002', 8000.00, 'INR', 'SUCCESS', '2024-02-01 14:15:00', '2024-02-01 14:16:45',
'{"gateway": "Razorpay", "gateway_txn_id": "pay_DEF987654321", "payment_method": "Net Banking", "bank": "HDFC Bank", "gateway_fee": 189.00, "net_amount": 7811.00}'),

-- TechnoElectronics payments
('c3d4e5f6-a7b8-9012-cdef-345678901234', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'f0a1b2c3-d4e5-6789-fabc-012345678901', 'TXN_TECH_MSIL_003', 8000.00, 'INR', 'SUCCESS', '2024-01-20 11:30:00', '2024-01-20 11:31:20',
'{"gateway": "PayU", "gateway_txn_id": "payu_GHI456789012", "payment_method": "Credit Card", "card_last4": "1234", "gateway_fee": 189.00, "net_amount": 7811.00}');

-- =====================================================
-- 9. API CREDENTIALS DATA
-- =====================================================

-- API credentials for active vendor-OEM combinations
INSERT INTO api_credentials (credential_id, vendor_id, oem_id, company_code, environment, api_key_hash, secret_encrypted, is_active, expires_at, last_rotated_at, rate_limits, usage_stats, created_at, updated_at, created_by, updated_by) VALUES
-- ABC Auto Parts with Maruti Suzuki (Production)
('d4e5f6a7-b8c9-0123-defa-456789012345', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'PRODUCTION',
'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjdigwDbvB4QWStormQrXtsRdnxPJTa',
'AES256:IV:1234567890abcdef:EncryptedSecretKey1234567890abcdef1234567890abcdef',
true, '2025-01-15 10:00:00', '2024-07-15 10:00:00',
'{"requests_per_minute": 500, "requests_per_day": 10000, "burst_limit": 100}',
'{"total_requests": 1250, "requests_today": 45, "last_request": "2024-09-15T08:30:00Z", "error_rate": 0.02}', NOW(), NOW(), NULL, NULL),

-- TechnoElectronics with Maruti Suzuki (Sandbox)
('e5f6a7b8-c9d0-1234-efab-567890123456', 'a7b8c9d0-e1f2-3456-abcd-789012345678', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 1001, 'SANDBOX',
'$2a$10$M8po7tKNjdhfx1YLQYnLxeHjcifvCabvA3PVstormPqWsrCmxOITb',
'AES256:IV:abcdef1234567890:EncryptedSandboxKey1234567890abcdef1234567890abcdef',
true, '2025-01-20 09:30:00', '2024-01-20 09:30:00',
'{"requests_per_minute": 100, "requests_per_day": 1000, "burst_limit": 50}',
'{"total_requests": 756, "requests_today": 12, "last_request": "2024-09-13T12:15:00Z", "error_rate": 0.05}', NOW(), NOW(), NULL, NULL);

-- =====================================================
-- 10. AUDIT LOGS DATA
-- =====================================================

-- Comprehensive audit logs for system activities
INSERT INTO audit_logs (audit_id, event_time, actor_id, actor_type, event_action, event_category, resource_type, resource_id, ip_address, request_id, session_id, changes) VALUES
-- Vendor registration events
('f6a7b8c9-d0e1-2345-fabc-678901234567', '2024-01-15 10:00:00', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'VENDOR', 'CREATE', 'REGISTRATION', 'VENDOR', 'f6a7b8c9-d0e1-2345-fabc-678901234567', '192.168.1.100', 'REQ-001', 'SESS-001',
'{"action": "vendor_registration", "company_name": "ABC Auto Parts Pvt Ltd", "pan_number": "ABCDE1234F", "contact_person": "Rajesh Kumar", "status": "pending_approval"}'),

-- API usage events
('a7b8c9d0-e1f2-3456-abcd-789012345678', '2024-09-15 08:30:15', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'VENDOR', 'CREATE', 'API_USAGE', 'ASN', NULL, '203.0.113.100', 'REQ-007', 'API-SESS-001',
'{"action": "asn_created", "asn_id": "ASN-MSIL-001", "invoice_number": "INV-ABC-001", "oem": "Maruti Suzuki", "api_endpoint": "/api/v1/asn/create", "response_time_ms": 245}'),

-- Payment events
('b8c9d0e1-f2a3-4567-bcde-890123456789', '2024-01-15 10:32:15', 'f6a7b8c9-d0e1-2345-fabc-678901234567', 'VENDOR', 'UPDATE', 'PAYMENT', 'PAYMENT_TRANSACTION', 'a1b2c3d4-e5f6-7890-abcd-123456789012', '192.168.1.100', 'REQ-006', 'SESS-001',
'{"action": "payment_completed", "transaction_ref": "TXN_ABC_MSIL_001", "amount": 15000.00, "currency": "INR", "payment_method": "UPI", "gateway": "Razorpay", "status": "success"}');

-- =====================================================
-- END OF SAMPLE DATA
-- =====================================================

-- Reset search path
RESET search_path;

-- Summary of inserted data
SELECT 'Sample data insertion completed successfully!' as status;
