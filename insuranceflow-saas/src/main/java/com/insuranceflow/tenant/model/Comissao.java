package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "comissao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comissao extends TenantBaseEntity {
    @Column(name = "apolice_id") private UUID apoliceId;
    @Column(name = "corretora_id") private UUID corretoraId;
    @Column(name = "seguradora_id") private UUID seguradoraId;
    @Column(nullable = false) private String tipo;
    private BigDecimal percentual;
    @Column(nullable = false) private BigDecimal valor;
    @Column(name = "data_referencia") private LocalDate dataReferencia;
    @Column(name = "data_pagamento") private LocalDate dataPagamento;
    @Column(nullable = false) private String status;
    private String observacoes;
}
