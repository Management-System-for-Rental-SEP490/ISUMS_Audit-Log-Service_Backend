CREATE TABLE IF NOT EXISTS audit_logs (
  id uuid PRIMARY KEY,
  event_id uuid UNIQUE NOT NULL,
  event_version integer NOT NULL DEFAULT 1,
  trace_id varchar(64),
  span_id varchar(64),
  request_id varchar(100),
  correlation_id varchar(100),
  actor_user_id varchar(100),
  actor_username varchar(255),
  actor_role varchar(100),
  actor_type varchar(50),
  tenant_id varchar(100),
  house_id varchar(100),
  action varchar(150) NOT NULL,
  resource_type varchar(100),
  resource_id varchar(150),
  service_name varchar(100) NOT NULL,
  status varchar(30) NOT NULL,
  client_ip varchar(64),
  source_ip varchar(64),
  user_agent text,
  cloudflare_ray_id varchar(100),
  metadata jsonb,
  error_code varchar(100),
  error_message text,
  idempotency_key varchar(150),
  occurred_at timestamptz NOT NULL,
  ingested_at timestamptz NOT NULL DEFAULT now(),
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_audit_actor_time ON audit_logs(actor_user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_action_time ON audit_logs(action, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX IF NOT EXISTS idx_audit_trace_id ON audit_logs(trace_id);
CREATE INDEX IF NOT EXISTS idx_audit_request_id ON audit_logs(request_id);
CREATE INDEX IF NOT EXISTS idx_audit_correlation_id ON audit_logs(correlation_id);
CREATE INDEX IF NOT EXISTS idx_audit_service_time ON audit_logs(service_name, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_status_time ON audit_logs(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_tenant_time ON audit_logs(tenant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_house_time ON audit_logs(house_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_metadata_gin ON audit_logs USING gin(metadata);
