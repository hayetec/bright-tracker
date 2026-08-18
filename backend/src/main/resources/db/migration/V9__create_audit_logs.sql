CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,

    actor_subject VARCHAR(255) NOT NULL,
    actor_username VARCHAR(255) NOT NULL,

    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id BIGINT,

    details TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_actor_subject
    ON audit_logs (actor_subject);

CREATE INDEX idx_audit_logs_resource
    ON audit_logs (resource_type, resource_id);

CREATE INDEX idx_audit_logs_created_at
    ON audit_logs (created_at);
