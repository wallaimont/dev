package com.insuranceflow.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAuditService {

    private final SecurityAuditLogRepository repository;

    @Async
    public void logEvent(String evento, UUID usuarioId, String usuarioEmail,
                         UUID empresaId, String ipAddress, String userAgent,
                         String detalhes, boolean sucesso) {
        try {
            SecurityAuditLog entry = SecurityAuditLog.builder()
                .evento(evento)
                .usuarioId(usuarioId)
                .usuarioEmail(maskEmail(usuarioEmail))
                .empresaId(empresaId)
                .ipAddress(ipAddress)
                .userAgent(truncate(userAgent, 500))
                .detalhes(detalhes)
                .sucesso(sucesso)
                .createdAt(LocalDateTime.now())
                .build();
            repository.save(entry);
        } catch (Exception e) {
            log.error("Falha ao registrar audit log: {}", e.getMessage());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
