package com.supportdesk.service;

import com.supportdesk.domain.entity.AuditLog;
import com.supportdesk.repository.AuditLogRepository;
import com.supportdesk.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String entityType, UUID entityId, String action,
                       UserPrincipal actor, Object beforeState, Object afterState) {
        try {
            AuditLog log = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .actorId(actor != null ? actor.getId() : null)
                    .actorEmail(actor != null ? actor.getEmail() : "system")
                    .beforeState(beforeState)
                    .afterState(afterState)
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to record audit log for {}/{}: {}", entityType, entityId, e.getMessage());
        }
    }
}
