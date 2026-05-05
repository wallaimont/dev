package com.insuranceflow.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {

    Page<SecurityAuditLog> findByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId, Pageable pageable);

    Page<SecurityAuditLog> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId, Pageable pageable);

    Page<SecurityAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SecurityAuditLog> findByEventoAndSucessoFalseOrderByCreatedAtDesc(String evento, Pageable pageable);

    long countByIpAddressAndEventoAndCreatedAtAfter(String ipAddress, String evento, LocalDateTime after);
}
