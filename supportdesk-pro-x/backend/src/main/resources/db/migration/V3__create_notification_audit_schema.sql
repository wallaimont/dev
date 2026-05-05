-- V3: Notifications, audit logs, event outbox

CREATE TYPE notification_type    AS ENUM ('TICKET_CREATED','TICKET_ASSIGNED','TICKET_STATUS_CHANGED','COMMENT_ADDED','SLA_WARNING','SLA_BREACHED');
CREATE TYPE notification_channel AS ENUM ('IN_APP','EMAIL','PUSH');
CREATE TYPE notification_status  AS ENUM ('PENDING','SENT','READ','FAILED');
CREATE TYPE outbox_status        AS ENUM ('PENDING','PUBLISHED','FAILED');

CREATE TABLE notifications (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id  UUID NOT NULL REFERENCES users(id),
    type          notification_type NOT NULL,
    channel       notification_channel NOT NULL DEFAULT 'IN_APP',
    status        notification_status NOT NULL DEFAULT 'PENDING',
    title         VARCHAR(255) NOT NULL,
    body          TEXT NOT NULL,
    entity_type   VARCHAR(50),
    entity_id     UUID,
    read_at       TIMESTAMPTZ,
    sent_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE notification_preferences (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type         notification_type NOT NULL,
    in_app       BOOLEAN NOT NULL DEFAULT TRUE,
    email        BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, type)
);

CREATE TABLE audit_logs (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type    VARCHAR(100) NOT NULL,
    entity_id      UUID NOT NULL,
    action         VARCHAR(100) NOT NULL,
    actor_id       UUID,
    actor_email    VARCHAR(255),
    before_state   JSONB,
    after_state    JSONB,
    diff           JSONB,
    correlation_id VARCHAR(36),
    ip_address     VARCHAR(45),
    user_agent     VARCHAR(500),
    occurred_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE event_outbox (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type  VARCHAR(100) NOT NULL,
    aggregate_id    UUID NOT NULL,
    event_type      VARCHAR(150) NOT NULL,
    payload         JSONB NOT NULL,
    status          outbox_status NOT NULL DEFAULT 'PENDING',
    kafka_topic     VARCHAR(200) NOT NULL,
    attempts        SMALLINT NOT NULL DEFAULT 0,
    max_attempts    SMALLINT NOT NULL DEFAULT 3,
    error_message   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    published_at    TIMESTAMPTZ,
    next_retry_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_recipient    ON notifications(recipient_id, status) WHERE status != 'READ';
CREATE INDEX idx_notifications_entity       ON notifications(entity_type, entity_id);
CREATE INDEX idx_audit_entity               ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_actor                ON audit_logs(actor_id);
CREATE INDEX idx_audit_occurred_at          ON audit_logs(occurred_at);
CREATE INDEX idx_outbox_pending             ON event_outbox(next_retry_at, status) WHERE status = 'PENDING';
CREATE INDEX idx_outbox_aggregate           ON event_outbox(aggregate_type, aggregate_id);
