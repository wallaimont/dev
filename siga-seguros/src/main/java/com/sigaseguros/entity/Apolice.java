package com.sigaseguros.entity;

import com.sigaseguros.enums.FormaPagamento;
import com.sigaseguros.enums.StatusApolice;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apolices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apolice extends BaseEntity {

    @Column(name = "numero_apolice", nullable = false, unique = true, length = 30)
    private String numeroApolice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguradora_id", nullable = false)
    private Seguradora seguradora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corretora_id")
    private Corretora corretora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ramo_seguro_id", nullable = false)
    private RamoSeguro ramoSeguro;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "inicio_vigencia", nullable = false)
    private LocalDate inicioVigencia;

    @Column(name = "fim_vigencia", nullable = false)
    private LocalDate fimVigencia;

    @Column(name = "premio_total", precision = 15, scale = 2)
    private BigDecimal premioTotal;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "valor_comissao", precision = 15, scale = 2)
    private BigDecimal valorComissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 20)
    private FormaPagamento formaPagamento;

    @Column(name = "quantidade_parcelas")
    private Integer quantidadeParcelas;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusApolice status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "responsavel_interno", length = 150)
    private String responsavelInterno;

    @OneToMany(mappedBy = "apolice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Documento> documentos = new ArrayList<>();
}
