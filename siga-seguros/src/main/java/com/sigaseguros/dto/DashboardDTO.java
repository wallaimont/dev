package com.sigaseguros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    // Cards
    private long totalClientes;
    private long propostasEmAberto;
    private long propostasAprovadas;
    private long apolicesAtivas;
    private long apolicesVencendo30Dias;
    private long sinistrosAbertos;
    private BigDecimal contasReceberPendentes;
    private BigDecimal contasPagarPendentes;
    private BigDecimal comissoesMes;

    // Graficos
    private Map<String, Long> propostasPorStatus;
    private Map<String, Long> apolicesPorSeguradora;
    private Map<String, BigDecimal> faturamentoPorMes;
    private Map<String, BigDecimal> comissoesPorMes;
    private Map<String, Long> sinistrosPorStatus;
    private Map<String, Long> renovacoesPorStatus;

    // Listagens rapidas
    private List<PropostaDTO> ultimasPropostas;
    private List<ApoliceDTO> apolicesVencendo;
    private List<SinistroDTO> sinistrosEmAberto;
}
