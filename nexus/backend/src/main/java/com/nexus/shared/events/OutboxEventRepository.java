package com.nexus.shared.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@org.springframework.stereotype.Repository
interface OutboxEventRepository extends org.springframework.data.jpa.repository.JpaRepository<OutboxEvent, UUID> {

    @org.springframework.data.jpa.repository.Query(
        "SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' " +
        "AND e.scheduledAt <= :now AND e.retryCount < e.maxRetries " +
        "ORDER BY e.scheduledAt ASC"
    )
    java.util.List<OutboxEvent> findPendingEvents(
        @org.springframework.data.repository.query.Param("now") Instant now,
        org.springframework.data.domain.Pageable pageable
    );

    boolean existsByIdempotencyKey(String key);
}
