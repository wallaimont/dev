package com.nexus.modules.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class LoggingPushChannel implements PushChannel {

    @Override
    public void send(UUID tenantId, UUID userId, String title, String body) {
        log.info("Push stub queued tenantId={}, userId={}, title={}", tenantId, userId, title);
    }
}