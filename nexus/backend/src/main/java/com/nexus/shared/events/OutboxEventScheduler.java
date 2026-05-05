package com.nexus.shared.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduler {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> pending = repository.findPendingEvents(
            Instant.now(),
            org.springframework.data.domain.PageRequest.of(0, BATCH_SIZE)
        );

        if (pending.isEmpty()) return;

        log.debug("Processing {} outbox events", pending.size());

        for (OutboxEvent event : pending) {
            try {
                event.setStatus(OutboxEvent.OutboxStatus.PROCESSING);
                repository.save(event);

                String topic = resolveKafkaTopic(event.getEventType());
                kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            event.setStatus(OutboxEvent.OutboxStatus.SENT);
                            event.setProcessedAt(Instant.now());
                        } else {
                            handleFailure(event, ex);
                        }
                        repository.save(event);
                    });

            } catch (Exception e) {
                handleFailure(event, e);
                repository.save(event);
            }
        }
    }

    private void handleFailure(OutboxEvent event, Throwable ex) {
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLastError(ex.getMessage());
        if (event.getRetryCount() >= event.getMaxRetries()) {
            event.setStatus(OutboxEvent.OutboxStatus.DEAD_LETTER);
            log.error("Event moved to DLQ: {} - {}", event.getId(), ex.getMessage());
        } else {
            event.setStatus(OutboxEvent.OutboxStatus.PENDING);
            long backoffSecs = (long) Math.pow(2, event.getRetryCount()) * 5L;
            event.setScheduledAt(Instant.now().plusSeconds(backoffSecs));
        }
    }

    private String resolveKafkaTopic(String eventType) {
        if (eventType.startsWith("Order")) return "nexus.orders";
        if (eventType.startsWith("Payment")) return "nexus.payments";
        if (eventType.startsWith("Seller")) return "nexus.sellers";
        if (eventType.startsWith("Product")) return "nexus.catalog";
        if (eventType.startsWith("Notification")) return "nexus.notifications";
        return "nexus.events";
    }
}
