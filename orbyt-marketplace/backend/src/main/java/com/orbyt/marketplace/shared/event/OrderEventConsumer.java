package com.orbyt.marketplace.shared.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "orbyt.order-created", groupId = "orbyt-notification-group")
    public void onOrderCreated(String payload) {
        log.info("Consumed order.created event: {}", payload);
        // Future: trigger notification, fraud analysis, etc.
    }

    @KafkaListener(topics = "orbyt.payment-approved", groupId = "orbyt-order-group")
    public void onPaymentApproved(String payload) {
        log.info("Consumed payment.approved event: {}", payload);
        // Future: update order status, trigger shipping, etc.
    }

    @KafkaListener(topics = "orbyt.order-shipped", groupId = "orbyt-notification-group")
    public void onOrderShipped(String payload) {
        log.info("Consumed order.shipped event: {}", payload);
        // Future: notify buyer
    }

    @KafkaListener(topics = "orbyt.seller-approved", groupId = "orbyt-notification-group")
    public void onSellerApproved(String payload) {
        log.info("Consumed seller.approved event: {}", payload);
    }
}
