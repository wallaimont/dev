package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lancamento_financeiro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LancamentoFinanceiro extends TenantBaseEntity {

    @Column(nullable = false) private String tipo;
    private String categoria;
    @Column(nullable = false) private String descricao;
    @Column(nullable = false) private BigDecimal valor;
    @Column(name = "data_vencimento", nullable = false) private LocalDate dataVencimento;
    @Column(name = "data_pagamento") private LocalDate dataPagamento;
    @Column(nullable = false) private String status;
    @Column(name = "forma_pagamento") private String formaPagamento;
    @Column(name = "cliente_id") private UUID clienteId;
    @Column(name = "apolice_id") private UUID apoliceId;
    @Column(name = "proposta_id") private UUID propostaId;
    private String observacoes;
}
