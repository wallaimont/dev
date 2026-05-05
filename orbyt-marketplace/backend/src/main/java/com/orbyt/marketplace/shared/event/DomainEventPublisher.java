package com.orbyt.marketplace.shared.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
