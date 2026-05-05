package com.orionerp.modules.contratos.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contratos")
@Getter
@Setter
public class Contrato extends TenantEntity {

    @Column(length = 30, nullable = false)
    private String numero;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(length = 30)
    private String tipo = "SERVICO";

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "data_assinatura")
    private LocalDate dataAssinatura;

    @Column(name = "valor_total", precision = 18, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_mensal", precision = 18, scale = 2)
    private BigDecimal valorMensal;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(length = 20)
    private String status = "RASCUNHO";

    @Column(name = "renovacao_automatica")
    private Boolean renovacaoAutomatica = false;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
