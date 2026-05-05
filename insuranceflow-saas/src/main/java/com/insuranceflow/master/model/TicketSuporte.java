package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_suporte")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketSuporte extends BaseEntity {

    @Column(name = "empresa_id")
    private UUID empresaId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(nullable = false)
    private String assunto;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String prioridade;

    @Column(nullable = false)
    private String status;

    private String categoria;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;
}
