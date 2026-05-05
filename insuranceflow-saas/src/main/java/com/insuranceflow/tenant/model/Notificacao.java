package com.insuranceflow.tenant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacao {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "empresa_id") private UUID empresaId;
    @Column(name = "usuario_id") private UUID usuarioId;
    @Column(nullable = false) private String titulo;
    @Column(nullable = false) private String mensagem;
    private String tipo;
    private Boolean lida;
    private String link;
    private Boolean active;
    @Column(name = "created_at") private LocalDateTime createdAt;
}
