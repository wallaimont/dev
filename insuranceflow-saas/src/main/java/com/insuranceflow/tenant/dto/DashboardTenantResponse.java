package com.insuranceflow.tenant.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardTenantResponse {
    private long totalClientes;
    private long totalPropostas;
    private long totalApolices;
    private long apolicesAtivas;
    private long sinistrosAbertos;
    private long renovacoesPendentes;
    private long boletosVencidos;
    private BigDecimal valorTotalPremios;
    private BigDecimal comissoesPendentes;
    private long leadsNovos;
}
