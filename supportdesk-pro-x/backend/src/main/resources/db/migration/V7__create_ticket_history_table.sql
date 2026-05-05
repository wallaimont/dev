-- V7: Create ticket_history table expected by JPA and migrate legacy rows

CREATE TABLE IF NOT EXISTS ticket_history (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id     UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    status_from   ticket_status,
    status_to     ticket_status,
    assignee_from UUID,
    assignee_to   UUID,
    changed_by    UUID NOT NULL,
    reason        VARCHAR(500),
    changed_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO ticket_history (id, ticket_id, changed_by, reason, changed_at)
SELECT th.id, th.ticket_id, th.actor_id, LEFT(th.note, 500), th.created_at
FROM ticket_histories th
WHERE NOT EXISTS (
    SELECT 1
    FROM ticket_history h
    WHERE h.id = th.id
);

CREATE INDEX IF NOT EXISTS idx_ticket_history_ticket ON ticket_history(ticket_id);
