package com.orbyt.marketplace.shared.event;

import com.orbyt.marketplace.operations.domain.OutboxEvent;
import com.orbyt.marketplace.operations.repository.OutboxEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : pending) {
            try {
                String topic = "orbyt." + event.getEventType().replace(".", "-");
                kafkaTemplate.send(topic, event.getAggregateId() != null ? event.getAggregateId().toString() : "", event.getPayload());
                event.setPublished(true);
                event.setPublishedAt(OffsetDateTime.now());
                event.setStatus("PUBLISHED");
                outboxEventRepository.save(event);
                log.debug("Published outbox event: {} -> topic {}", event.getEventType(), topic);
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setNextRetryAt(OffsetDateTime.now().plusSeconds(event.getRetryCount() * 30L));
                if (event.getRetryCount() >= 5) {
                    event.setStatus("DEAD_LETTER");
                    log.error("Event moved to dead letter after {} retries: {}", event.getRetryCount(), event.getEventType());
                }
                outboxEventRepository.save(event);
            }
        }
    }
}
