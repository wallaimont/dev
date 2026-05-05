package com.insuranceflow.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "security_audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String evento;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "usuario_email")
    private String usuarioEmail;

    @Column(name = "empresa_id")
    private UUID empresaId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    private String detalhes;

    @Column(nullable = false)
    private Boolean sucesso;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
