package com.supportdesk.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportdesk.domain.entity.EventOutbox;
import com.supportdesk.domain.enums.OutboxStatus;
import com.supportdesk.repository.EventOutboxRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPollingService {

    private final EventOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedDelayString = "${app.outbox.polling-interval-ms:5000}")
    @Transactional
    public void process() {
        Counter publishedCounter = meterRegistry.counter("sdpx.outbox.published.count");
        Counter failedCounter = meterRegistry.counter("sdpx.outbox.failed.count");
        Counter retryCounter = meterRegistry.counter("sdpx.outbox.retry.count");
        List<EventOutbox> events = outboxRepository.findDueForProcessing(OutboxStatus.PENDING, Instant.now());
        for (EventOutbox event : events) {
            try {
                String payload = objectMapper.writeValueAsString(event.getPayload());
                kafkaTemplate.send(event.getKafkaTopic(), event.getAggregateId().toString(), payload).get();
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                publishedCounter.increment();
                log.debug("Published outbox eventId={} aggregateId={} topic={}", event.getId(), event.getAggregateId(), event.getKafkaTopic());
            } catch (Exception e) {
                short newAttempts = (short) (event.getAttempts() + 1);
                event.setAttempts(newAttempts);
                event.setErrorMessage(e.getMessage());
                if (newAttempts >= event.getMaxAttempts()) {
                    event.setStatus(OutboxStatus.FAILED);
                    failedCounter.increment();
                    log.error("Outbox eventId={} permanently failed after {} attempts", event.getId(), newAttempts);
                } else {
                    // Exponential backoff: 30s, 2min, 10min
                    long backoffSeconds = (long) Math.pow(4, newAttempts) * 30L;
                    event.setNextRetryAt(Instant.now().plus(backoffSeconds, ChronoUnit.SECONDS));
                    retryCounter.increment();
                    log.warn("Outbox eventId={} attempt {}/{} failed, retry at {}", event.getId(), newAttempts, event.getMaxAttempts(), event.getNextRetryAt());
                }
            }
            outboxRepository.save(event);
        }
    }
}
