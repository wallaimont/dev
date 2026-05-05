package com.insuranceflow.master.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardMasterResponse {
    private long totalEmpresas;
    private long empresasAtivas;
    private long empresasTrial;
    private long empresasSuspensas;
    private long empresasCanceladas;
    private BigDecimal mrr;
    private BigDecimal arr;
    private long totalUsuarios;
    private long totalPropostas;
    private long totalApolices;
    private long ticketsAbertos;
    private long leadsNovos;
    private long pagamentosPendentes;
}
