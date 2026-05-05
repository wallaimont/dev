package com.insuranceflow.master.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_plano")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoricoPlano {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "plano_anterior_id")
    private UUID planoAnteriorId;

    @Column(name = "plano_novo_id", nullable = false)
    private UUID planoNovoId;

    @Column(name = "tipo_alteracao", nullable = false)
    private String tipoAlteracao;

    private String observacoes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
