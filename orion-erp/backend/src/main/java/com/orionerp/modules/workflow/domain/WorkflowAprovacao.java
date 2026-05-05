package com.orionerp.modules.workflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_aprovacoes")
@Getter
@Setter
public class WorkflowAprovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "filial_id", nullable = false)
    private Long filialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definicao_id", nullable = false)
    private WorkflowDefinicao workflowDefinicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alcada_id", nullable = false)
    private WorkflowAlcada alcada;

    @Column(nullable = false, length = 100)
    private String entidade;

    @Column(name = "entidade_id", nullable = false)
    private Long entidadeId;

    @Column(nullable = false)
    private Integer nivel;

    @Column(name = "aprovador_id")
    private Long aprovadorId;

    @Column(length = 20)
    private String decisao;

    @Column(columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "data_decisao")
    private LocalDateTime dataDecisao;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
