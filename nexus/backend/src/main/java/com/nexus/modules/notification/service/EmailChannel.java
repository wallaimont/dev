package com.nexus.modules.notification.service;

import java.util.UUID;

public interface EmailChannel {
    void send(UUID tenantId, UUID userId, String template, Object data);
}
