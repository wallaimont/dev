-- V2: Support domain — categories, tickets, histories, comments, attachments

CREATE TYPE ticket_status   AS ENUM ('OPEN','IN_PROGRESS','PENDING','RESOLVED','CLOSED','CANCELLED');
CREATE TYPE ticket_priority AS ENUM ('LOW','MEDIUM','HIGH','CRITICAL');

CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    color       VARCHAR(7),
    icon        VARCHAR(50),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE tickets (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_number    VARCHAR(20) NOT NULL UNIQUE,
    title            VARCHAR(300) NOT NULL,
    description      TEXT NOT NULL,
    status           ticket_status NOT NULL DEFAULT 'OPEN',
    priority         ticket_priority NOT NULL DEFAULT 'MEDIUM',
    category_id      UUID REFERENCES categories(id),
    reporter_id      UUID NOT NULL REFERENCES users(id),
    assignee_id      UUID REFERENCES users(id),
    tags             TEXT[] NOT NULL DEFAULT '{}',
    metadata         JSONB NOT NULL DEFAULT '{}',
    sla_deadline_at  TIMESTAMPTZ,
    sla_breached     BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at      TIMESTAMPTZ,
    closed_at        TIMESTAMPTZ,
    due_at           TIMESTAMPTZ,
    idempotency_key  VARCHAR(100) UNIQUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at       TIMESTAMPTZ,
    created_by       UUID,
    updated_by       UUID,
    deleted_by       UUID
);

CREATE SEQUENCE ticket_seq START 1 INCREMENT 1;

CREATE TABLE ticket_histories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    actor_id    UUID NOT NULL REFERENCES users(id),
    field_name  VARCHAR(100) NOT NULL,
    old_value   TEXT,
    new_value   TEXT,
    note        TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE comments (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    author_id   UUID NOT NULL REFERENCES users(id),
    content     TEXT NOT NULL,
    internal    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ
);

CREATE TABLE attachments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id    UUID REFERENCES tickets(id) ON DELETE CASCADE,
    comment_id   UUID REFERENCES comments(id) ON DELETE CASCADE,
    uploader_id  UUID NOT NULL REFERENCES users(id),
    file_name    VARCHAR(255) NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    content_type VARCHAR(127) NOT NULL,
    size_bytes   BIGINT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_attachment_target CHECK (
        (ticket_id IS NOT NULL AND comment_id IS NULL)
        OR (ticket_id IS NULL AND comment_id IS NOT NULL)
    )
);

CREATE INDEX idx_tickets_status       ON tickets(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_priority     ON tickets(priority) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_reporter     ON tickets(reporter_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_assignee     ON tickets(assignee_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_category     ON tickets(category_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_tags         ON tickets USING GIN(tags) WHERE deleted_at IS NULL;
CREATE INDEX idx_tickets_sla_breach   ON tickets(sla_deadline_at) WHERE sla_breached = FALSE AND status NOT IN ('RESOLVED','CLOSED','CANCELLED');
CREATE INDEX idx_histories_ticket     ON ticket_histories(ticket_id);
CREATE INDEX idx_comments_ticket      ON comments(ticket_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_attachments_ticket   ON attachments(ticket_id);
CREATE INDEX idx_attachments_comment  ON attachments(comment_id);
