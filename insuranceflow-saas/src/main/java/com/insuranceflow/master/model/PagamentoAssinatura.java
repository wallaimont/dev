package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pagamento_assinatura")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PagamentoAssinatura extends BaseEntity {

    @Column(name = "assinatura_id", nullable = false)
    private UUID assinaturaId;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(nullable = false)
    private String status;

    @Column(name = "metodo_pagamento")
    private String metodoPagamento;

    @Column(name = "id_transacao_externa")
    private String idTransacaoExterna;

    @Column(name = "link_cobranca")
    private String linkCobranca;

    private String observacoes;
}
