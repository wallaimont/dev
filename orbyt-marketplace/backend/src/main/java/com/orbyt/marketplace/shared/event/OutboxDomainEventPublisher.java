package com.orbyt.marketplace.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.operations.domain.OutboxEvent;
import com.orbyt.marketplace.operations.repository.OutboxEventRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void publish(DomainEvent event) {
        try {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setTenantId(TenantContext.require());
            outbox.setEventType(event.eventType());
            outbox.setAggregateType(event.aggregateType());
            outbox.setAggregateId(event.aggregateId());
            outbox.setPayload(objectMapper.writeValueAsString(event.payload()));
            outbox.setPublished(false);
            outbox.setRetryCount(0);
            outboxEventRepository.save(outbox);
            log.info("Domain event stored in outbox: type={}, aggregateId={}", event.eventType(), event.aggregateId());
        } catch (Exception e) {
            log.error("Failed to store domain event in outbox: {}", event.eventType(), e);
            throw new RuntimeException("Failed to store event", e);
        }
    }
}
