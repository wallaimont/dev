package com.insuranceflow.master.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class BillingDashboardResponse {
    private long assinaturasAtivas;
    private long assinaturasTrial;
    private long assinaturasAtrasadas;
    private long assinaturasCanceladas;
    private BigDecimal mrr;
    private BigDecimal arr;
    private BigDecimal totalRecebido;
    private BigDecimal totalPendente;
    private long pagamentosPendentes;
    private long pagamentosAtrasados;
}
