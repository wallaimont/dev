package com.insuranceflow.tenant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Auditoria {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "empresa_id") private UUID empresaId;
    @Column(name = "usuario_id") private UUID usuarioId;
    @Column(name = "usuario_email") private String usuarioEmail;
    @Column(nullable = false) private String acao;
    @Column(nullable = false) private String entidade;
    @Column(name = "entidade_id") private UUID entidadeId;
    @Column(name = "dados_anteriores", columnDefinition = "jsonb") private String dadosAnteriores;
    @Column(name = "dados_novos", columnDefinition = "jsonb") private String dadosNovos;
    private String ip;
    @Column(name = "user_agent") private String userAgent;
    @Column(name = "created_at") private LocalDateTime createdAt;
}
