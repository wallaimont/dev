package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assinatura_empresa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssinaturaEmpresa extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "plano_id", nullable = false)
    private UUID planoId;

    @Column(nullable = false)
    private String ciclo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String status;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_vencimento")
    private LocalDateTime dataVencimento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "id_assinatura_externa")
    private String idAssinaturaExterna;

    @Column(name = "gateway_pagamento")
    private String gatewayPagamento;

    private String observacoes;
}
