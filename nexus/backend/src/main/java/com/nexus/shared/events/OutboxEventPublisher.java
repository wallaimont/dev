package com.nexus.shared.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(Object event, String idempotencyKey) {
        try {
            String eventType = event.getClass().getSimpleName();
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = new OutboxEvent();
            outbox.setAggregateType(eventType);
            outbox.setEventType(eventType);
            outbox.setPayload(payload);

            outboxEventRepository.save(outbox);
            log.debug("Outbox event published: type={} key={}", eventType, idempotencyKey);
        } catch (Exception e) {
            log.error("Failed to publish outbox event", e);
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
