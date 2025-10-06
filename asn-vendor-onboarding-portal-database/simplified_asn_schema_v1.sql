-- DROP SCHEMA sch_asn_vendor_onboarding_portal;

CREATE SCHEMA sch_asn_vendor_onboarding_portal AUTHORIZATION postgres;
-- sch_asn_vendor_onboarding_portal.audit_logs definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.audit_logs;

CREATE TABLE sch_asn_vendor_onboarding_portal.audit_logs (
	event_time timestamp(6) NOT NULL,
	actor_id uuid NULL,
	audit_id uuid NOT NULL,
	resource_id uuid NULL,
	ip_address inet NULL,
	actor_type varchar(20) NULL,
	event_action varchar(50) NOT NULL,
	event_category varchar(50) NOT NULL,
	resource_type varchar(50) NULL,
	request_id varchar(100) NULL,
	session_id varchar(100) NULL,
	changes jsonb NULL,
	CONSTRAINT audit_logs_actor_type_check CHECK (((actor_type)::text = ANY ((ARRAY['SYSTEM'::character varying, 'VENDOR'::character varying, 'ADMIN'::character varying])::text[]))),
	CONSTRAINT audit_logs_pkey PRIMARY KEY (audit_id)
);
CREATE INDEX idx_audit_logs_actor ON sch_asn_vendor_onboarding_portal.audit_logs USING btree (actor_id);
CREATE INDEX idx_audit_logs_resource ON sch_asn_vendor_onboarding_portal.audit_logs USING btree (resource_type, resource_id);
CREATE INDEX idx_audit_logs_timestamp ON sch_asn_vendor_onboarding_portal.audit_logs USING btree (event_time);


-- sch_asn_vendor_onboarding_portal.oem_master definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.oem_master;

CREATE TABLE sch_asn_vendor_onboarding_portal.oem_master (
	asn_deadline date NULL,
	go_live_date date NULL,
	priority_rank int4 NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	created_by uuid NULL,
	oem_id uuid NOT NULL,
	updated_by uuid NULL,
	asn_version varchar(20) NOT NULL,
	oem_code varchar(20) NOT NULL,
	status varchar(20) NULL,
	oem_name varchar(100) NOT NULL,
	full_name varchar(200) NOT NULL,
	config jsonb NOT NULL,
	CONSTRAINT idx_oem_master_code UNIQUE (oem_code),
	CONSTRAINT oem_master_oem_code_key UNIQUE (oem_code),
	CONSTRAINT oem_master_pkey PRIMARY KEY (oem_id),
	CONSTRAINT oem_master_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'SUSPENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'COMING_SOON'::character varying, 'DEPRECATED'::character varying])::text[])))
);
CREATE INDEX idx_oem_master_status ON sch_asn_vendor_onboarding_portal.oem_master USING btree (status);


-- sch_asn_vendor_onboarding_portal.system_config definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.system_config;

CREATE TABLE sch_asn_vendor_onboarding_portal.system_config (
	is_encrypted bool NOT NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	config_id uuid NOT NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	config_type varchar(50) NOT NULL,
	config_key varchar(100) NOT NULL,
	config_value jsonb NOT NULL,
	CONSTRAINT idx_system_config_key UNIQUE (config_key),
	CONSTRAINT system_config_config_key_key UNIQUE (config_key),
	CONSTRAINT system_config_pkey PRIMARY KEY (config_id)
);


-- sch_asn_vendor_onboarding_portal.vendors definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.vendors;

CREATE TABLE sch_asn_vendor_onboarding_portal.vendors (
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	last_activity_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	pan_number varchar(10) NOT NULL,
	created_by uuid NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	status varchar(20) NULL,
	cin_number varchar(21) NULL,
	company_name varchar(200) NOT NULL,
	auth_credentials jsonb NOT NULL,
	primary_contact jsonb NOT NULL,
	CONSTRAINT idx_vendors_pan UNIQUE (pan_number),
	CONSTRAINT vendors_pan_number_key UNIQUE (pan_number),
	CONSTRAINT vendors_pkey PRIMARY KEY (vendor_id),
	CONSTRAINT vendors_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'SUSPENDED'::character varying, 'PENDING_APPROVAL'::character varying, 'COMING_SOON'::character varying, 'DEPRECATED'::character varying])::text[])))
);
CREATE INDEX idx_vendors_company_name ON sch_asn_vendor_onboarding_portal.vendors USING btree (company_name);
CREATE INDEX idx_vendors_status ON sch_asn_vendor_onboarding_portal.vendors USING btree (status);


-- sch_asn_vendor_onboarding_portal.api_credentials definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.api_credentials;

CREATE TABLE sch_asn_vendor_onboarding_portal.api_credentials (
	is_active bool NOT NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	expires_at timestamp(6) NULL,
	last_rotated_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	created_by uuid NULL,
	credential_id uuid NOT NULL,
	oem_id uuid NOT NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	environment varchar(20) NULL,
	api_key_hash varchar(255) NOT NULL,
	secret_encrypted text NOT NULL,
	rate_limits jsonb NOT NULL,
	usage_stats jsonb NULL,
	CONSTRAINT api_credentials_api_key_hash_key UNIQUE (api_key_hash),
	CONSTRAINT api_credentials_environment_check CHECK (((environment)::text = ANY ((ARRAY['SANDBOX'::character varying, 'PRODUCTION'::character varying])::text[]))),
	CONSTRAINT api_credentials_pkey PRIMARY KEY (credential_id),
	CONSTRAINT uk_api_credentials_vendor_oem_env UNIQUE (vendor_id, oem_id, environment),
	CONSTRAINT fk_api_credentials_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id),
	CONSTRAINT fk_api_credentials_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_api_credentials_active ON sch_asn_vendor_onboarding_portal.api_credentials USING btree (is_active);
CREATE INDEX idx_api_credentials_vendor_oem ON sch_asn_vendor_onboarding_portal.api_credentials USING btree (vendor_id, oem_id);


-- sch_asn_vendor_onboarding_portal.api_request_logs definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.api_request_logs;

CREATE TABLE sch_asn_vendor_onboarding_portal.api_request_logs (
	response_time_ms int4 NULL,
	status_code int2 NULL,
	request_timestamp timestamp(6) NOT NULL,
	"method" varchar(10) NOT NULL,
	credential_id uuid NOT NULL,
	log_id uuid NOT NULL,
	error_code varchar(50) NULL,
	endpoint varchar(100) NOT NULL,
	request_data jsonb NULL,
	response_data jsonb NULL,
	CONSTRAINT api_request_logs_pkey PRIMARY KEY (log_id),
	CONSTRAINT fk_api_request_logs_credential FOREIGN KEY (credential_id) REFERENCES sch_asn_vendor_onboarding_portal.api_credentials(credential_id)
);
CREATE INDEX idx_api_request_logs_credential ON sch_asn_vendor_onboarding_portal.api_request_logs USING btree (credential_id);
CREATE INDEX idx_api_request_logs_timestamp ON sch_asn_vendor_onboarding_portal.api_request_logs USING btree (request_timestamp);


-- sch_asn_vendor_onboarding_portal.onboarding_process definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.onboarding_process;

CREATE TABLE sch_asn_vendor_onboarding_portal.onboarding_process (
	progress_percentage int4 NULL,
	company_code int8 NOT NULL,
	completed_at timestamp(6) NULL,
	created_at timestamp(6) NULL,
	last_updated_at timestamp(6) NULL,
	started_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	created_by uuid NULL,
	initiated_by uuid NULL,
	last_updated_by uuid NULL,
	oem_id uuid NOT NULL,
	onboarding_id uuid NOT NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	status varchar(20) NULL,
	current_step varchar(50) NOT NULL,
	deployment_method varchar(50) NULL,
	step_data jsonb NULL,
	steps_completed jsonb NULL,
	CONSTRAINT onboarding_process_pkey PRIMARY KEY (onboarding_id),
	CONSTRAINT onboarding_process_progress_percentage_check CHECK (((progress_percentage <= 100) AND (progress_percentage >= 0))),
	CONSTRAINT onboarding_process_status_check CHECK (((status)::text = ANY ((ARRAY['NOT_STARTED'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'CANCELLED'::character varying])::text[]))),
	CONSTRAINT uk_onboarding_process_vendor_oem UNIQUE (vendor_id, oem_id),
	CONSTRAINT fk_onboarding_process_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id),
	CONSTRAINT fk_onboarding_process_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_onboarding_process_oem ON sch_asn_vendor_onboarding_portal.onboarding_process USING btree (oem_id);
CREATE INDEX idx_onboarding_process_status ON sch_asn_vendor_onboarding_portal.onboarding_process USING btree (status);
CREATE INDEX idx_onboarding_process_vendor ON sch_asn_vendor_onboarding_portal.onboarding_process USING btree (vendor_id);


-- sch_asn_vendor_onboarding_portal.subscription_plans definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.subscription_plans;

CREATE TABLE sch_asn_vendor_onboarding_portal.subscription_plans (
	display_order int4 NULL,
	is_active bool NOT NULL,
	is_featured bool NOT NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	created_by uuid NULL,
	oem_id uuid NOT NULL,
	plan_id uuid NOT NULL,
	updated_by uuid NULL,
	plan_code varchar(20) NOT NULL,
	plan_name varchar(50) NOT NULL,
	api_limits jsonb NOT NULL,
	features jsonb NOT NULL,
	pricing jsonb NOT NULL,
	support_config jsonb NOT NULL,
	CONSTRAINT subscription_plans_pkey PRIMARY KEY (plan_id),
	CONSTRAINT uk_subscription_plans_oem_code UNIQUE (oem_id, plan_code),
	CONSTRAINT fk_subscription_plans_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id)
);
CREATE INDEX idx_subscription_plans_active ON sch_asn_vendor_onboarding_portal.subscription_plans USING btree (is_active);
CREATE INDEX idx_subscription_plans_oem ON sch_asn_vendor_onboarding_portal.subscription_plans USING btree (oem_id);


-- sch_asn_vendor_onboarding_portal.subscriptions definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.subscriptions;

CREATE TABLE sch_asn_vendor_onboarding_portal.subscriptions (
	auto_renew bool NOT NULL,
	end_date date NOT NULL,
	next_billing_date date NULL,
	start_date date NOT NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	created_by uuid NULL,
	oem_id uuid NOT NULL,
	plan_id uuid NOT NULL,
	subscription_id uuid NOT NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	status varchar(20) NULL,
	pricing_snapshot jsonb NOT NULL,
	CONSTRAINT subscriptions_pkey PRIMARY KEY (subscription_id),
	CONSTRAINT subscriptions_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying, 'SUSPENDED'::character varying])::text[]))),
	CONSTRAINT uk_subscriptions_vendor_oem UNIQUE (vendor_id, oem_id),
	CONSTRAINT fk_subscriptions_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id),
	CONSTRAINT fk_subscriptions_plan FOREIGN KEY (plan_id) REFERENCES sch_asn_vendor_onboarding_portal.subscription_plans(plan_id),
	CONSTRAINT fk_subscriptions_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_subscriptions_dates ON sch_asn_vendor_onboarding_portal.subscriptions USING btree (start_date, end_date);
CREATE INDEX idx_subscriptions_oem ON sch_asn_vendor_onboarding_portal.subscriptions USING btree (oem_id);
CREATE INDEX idx_subscriptions_status ON sch_asn_vendor_onboarding_portal.subscriptions USING btree (status);
CREATE INDEX idx_subscriptions_vendor ON sch_asn_vendor_onboarding_portal.subscriptions USING btree (vendor_id);


-- sch_asn_vendor_onboarding_portal.vendor_gstin definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.vendor_gstin;

CREATE TABLE sch_asn_vendor_onboarding_portal.vendor_gstin (
	is_primary bool NOT NULL,
	is_verified bool NOT NULL,
	state_code varchar(2) NOT NULL,
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	verified_at timestamp(6) NULL,
	gstin varchar(15) NOT NULL,
	created_by uuid NULL,
	gstin_id uuid NOT NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	CONSTRAINT vendor_gstin_gstin_key UNIQUE (gstin),
	CONSTRAINT vendor_gstin_pkey PRIMARY KEY (gstin_id),
	CONSTRAINT fk_vendor_gstin_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_vendor_gstin_primary ON sch_asn_vendor_onboarding_portal.vendor_gstin USING btree (vendor_id, is_primary);
CREATE INDEX idx_vendor_gstin_vendor ON sch_asn_vendor_onboarding_portal.vendor_gstin USING btree (vendor_id);


-- sch_asn_vendor_onboarding_portal.vendor_oem_access definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.vendor_oem_access;

CREATE TABLE sch_asn_vendor_onboarding_portal.vendor_oem_access (
	company_code int8 NOT NULL,
	created_at timestamp(6) NULL,
	expires_at timestamp(6) NULL,
	granted_at timestamp(6) NULL,
	last_accessed_at timestamp(6) NULL,
	total_api_calls int8 NULL,
	total_asn_generated int8 NULL,
	updated_at timestamp(6) NULL,
	access_id uuid NOT NULL,
	created_by uuid NULL,
	oem_id uuid NOT NULL,
	updated_by uuid NULL,
	vendor_id uuid NOT NULL,
	access_level varchar(20) NULL,
	access_status varchar(20) NULL,
	vendor_code varchar(20) NOT NULL,
	permissions_cache jsonb NULL,
	CONSTRAINT uk_vendor_oem_access_oem_vendor_code UNIQUE (oem_id, vendor_code),
	CONSTRAINT uk_vendor_oem_access_vendor_oem UNIQUE (vendor_id, oem_id),
	CONSTRAINT vendor_oem_access_access_level_check CHECK (((access_level)::text = ANY ((ARRAY['BASIC'::character varying, 'ADVANCED'::character varying, 'PREMIUM'::character varying, 'READ_ONLY'::character varying])::text[]))),
	CONSTRAINT vendor_oem_access_access_status_check CHECK (((access_status)::text = ANY ((ARRAY['PENDING'::character varying, 'ACTIVE'::character varying, 'SUSPENDED'::character varying, 'REVOKED'::character varying])::text[]))),
	CONSTRAINT vendor_oem_access_pkey PRIMARY KEY (access_id),
	CONSTRAINT fk_vendor_oem_access_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id),
	CONSTRAINT fk_vendor_oem_access_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_vendor_oem_access_oem ON sch_asn_vendor_onboarding_portal.vendor_oem_access USING btree (oem_id);
CREATE INDEX idx_vendor_oem_access_status ON sch_asn_vendor_onboarding_portal.vendor_oem_access USING btree (access_status);
CREATE INDEX idx_vendor_oem_access_vendor ON sch_asn_vendor_onboarding_portal.vendor_oem_access USING btree (vendor_id);


-- sch_asn_vendor_onboarding_portal.onboarding_events definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.onboarding_events;

CREATE TABLE sch_asn_vendor_onboarding_portal.onboarding_events (
	event_timestamp timestamp(6) NOT NULL,
	event_id uuid NOT NULL,
	onboarding_id uuid NOT NULL,
	triggered_by uuid NULL,
	triggered_by_type varchar(20) NULL,
	event_type varchar(50) NOT NULL,
	idempotency_key varchar(100) NULL,
	event_data jsonb NOT NULL,
	CONSTRAINT onboarding_events_pkey PRIMARY KEY (event_id),
	CONSTRAINT onboarding_events_triggered_by_type_check CHECK (((triggered_by_type)::text = ANY ((ARRAY['SYSTEM'::character varying, 'VENDOR'::character varying, 'ADMIN'::character varying])::text[]))),
	CONSTRAINT fk_onboarding_events_onboarding FOREIGN KEY (onboarding_id) REFERENCES sch_asn_vendor_onboarding_portal.onboarding_process(onboarding_id)
);
CREATE INDEX idx_onboarding_events_onboarding ON sch_asn_vendor_onboarding_portal.onboarding_events USING btree (onboarding_id);
CREATE INDEX idx_onboarding_events_timestamp ON sch_asn_vendor_onboarding_portal.onboarding_events USING btree (event_timestamp);


-- sch_asn_vendor_onboarding_portal.payment_transactions definition

-- Drop table

-- DROP TABLE sch_asn_vendor_onboarding_portal.payment_transactions;

CREATE TABLE sch_asn_vendor_onboarding_portal.payment_transactions (
	amount numeric(10, 2) NOT NULL,
	currency varchar(3) NULL,
	completed_at timestamp(6) NULL,
	initiated_at timestamp(6) NOT NULL,
	oem_id uuid NOT NULL,
	subscription_id uuid NULL,
	transaction_id uuid NOT NULL,
	vendor_id uuid NOT NULL,
	status varchar(20) NULL,
	transaction_ref varchar(50) NOT NULL,
	gateway_data jsonb NOT NULL,
	CONSTRAINT payment_transactions_pkey PRIMARY KEY (transaction_id),
	CONSTRAINT payment_transactions_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying, 'REFUNDED'::character varying])::text[]))),
	CONSTRAINT payment_transactions_transaction_ref_key UNIQUE (transaction_ref),
	CONSTRAINT fk_payment_transactions_oem FOREIGN KEY (oem_id) REFERENCES sch_asn_vendor_onboarding_portal.oem_master(oem_id),
	CONSTRAINT fk_payment_transactions_subscription FOREIGN KEY (subscription_id) REFERENCES sch_asn_vendor_onboarding_portal.subscriptions(subscription_id),
	CONSTRAINT fk_payment_transactions_vendor FOREIGN KEY (vendor_id) REFERENCES sch_asn_vendor_onboarding_portal.vendors(vendor_id)
);
CREATE INDEX idx_payment_transactions_date ON sch_asn_vendor_onboarding_portal.payment_transactions USING btree (initiated_at);
CREATE INDEX idx_payment_transactions_status ON sch_asn_vendor_onboarding_portal.payment_transactions USING btree (status);
CREATE INDEX idx_payment_transactions_subscription ON sch_asn_vendor_onboarding_portal.payment_transactions USING btree (subscription_id);
CREATE INDEX idx_payment_transactions_vendor ON sch_asn_vendor_onboarding_portal.payment_transactions USING btree (vendor_id);