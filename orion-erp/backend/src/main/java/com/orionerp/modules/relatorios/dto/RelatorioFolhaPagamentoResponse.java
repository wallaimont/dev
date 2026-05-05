package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioFolhaPagamentoResponse(
        String competencia,
        Long totalFuncionarios,
        BigDecimal totalBruto,
        BigDecimal totalDescontos,
        BigDecimal totalLiquido,
        BigDecimal totalFgts,
        BigDecimal totalInssEmpresa
) {}
