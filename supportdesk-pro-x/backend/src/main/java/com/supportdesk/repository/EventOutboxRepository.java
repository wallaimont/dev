package com.supportdesk.repository;

import com.supportdesk.domain.entity.EventOutbox;
import com.supportdesk.domain.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, UUID> {

    @Query("SELECT e FROM EventOutbox e WHERE e.status = :status AND e.nextRetryAt <= :now ORDER BY e.createdAt ASC")
    List<EventOutbox> findDueForProcessing(OutboxStatus status, Instant now);
}
