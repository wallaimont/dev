package com.supportdesk.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportdesk.domain.entity.EventOutbox;
import com.supportdesk.domain.enums.OutboxStatus;
import com.supportdesk.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final EventOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void publish(String aggregateType, UUID aggregateId, String eventType,
                        String kafkaTopic, Map<String, Object> payload) {
        try {
            EventOutbox event = EventOutbox.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .kafkaTopic(kafkaTopic)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .attempts((short) 0)
                    .maxAttempts((short) 3)
                    .nextRetryAt(Instant.now())
                    .build();
            outboxRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to save outbox event type={} aggregateId={}: {}", eventType, aggregateId, e.getMessage());
        }
    }
}
