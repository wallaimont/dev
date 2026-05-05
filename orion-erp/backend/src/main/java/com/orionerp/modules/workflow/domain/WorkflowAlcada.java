package com.orionerp.modules.workflow.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_alcadas")
@Getter
@Setter
public class WorkflowAlcada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definicao_id", nullable = false)
    private WorkflowDefinicao workflowDefinicao;

    @Column(nullable = false)
    private Integer nivel;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "perfil_id")
    private Long perfilId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "valor_minimo", precision = 18, scale = 2)
    private BigDecimal valorMinimo;

    @Column(name = "valor_maximo", precision = 18, scale = 2)
    private BigDecimal valorMaximo;

    @Column(nullable = false)
    private Boolean obrigatorio = true;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
