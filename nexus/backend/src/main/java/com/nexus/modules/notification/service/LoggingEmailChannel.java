package com.nexus.modules.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class LoggingEmailChannel implements EmailChannel {

    @Override
    public void send(UUID tenantId, UUID userId, String template, Object data) {
        log.info("Email stub queued tenantId={}, userId={}, template={}", tenantId, userId, template);
    }
}