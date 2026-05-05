package com.orionerp.modules.contratos.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contrato_parcelas")
@Getter
@Setter
public class ContratoParcela extends TenantEntity {

    @Column(name = "contrato_id", nullable = false)
    private Long contratoId;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(precision = 18, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_pago", precision = 18, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(length = 20)
    private String status = "PENDENTE";
}
