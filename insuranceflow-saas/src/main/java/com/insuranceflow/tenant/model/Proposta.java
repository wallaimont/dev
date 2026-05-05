package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "proposta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Proposta extends TenantBaseEntity {

    private String numero;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "seguradora_id") private UUID seguradoraId;
    @Column(name = "corretora_id") private UUID corretoraId;
    @Column(name = "ramo_id") private UUID ramoId;

    @Column(name = "tipo_seguro") private String tipoSeguro;

    @Column(nullable = false) private String status;

    @Column(name = "data_proposta", nullable = false) private LocalDate dataProposta;
    @Column(name = "data_inicio_vigencia") private LocalDate dataInicioVigencia;
    @Column(name = "data_fim_vigencia") private LocalDate dataFimVigencia;

    @Column(name = "valor_importancia_segurada") private BigDecimal valorImportanciaSegurada;
    @Column(name = "valor_premio") private BigDecimal valorPremio;
    @Column(name = "valor_premio_liquido") private BigDecimal valorPremioLiquido;
    @Column(name = "valor_iof") private BigDecimal valorIof;

    @Column(name = "forma_pagamento") private String formaPagamento;
    @Column(name = "numero_parcelas") private Integer numeroParcelas;
    private String observacoes;
    @Column(name = "motivo_recusa") private String motivoRecusa;
}
