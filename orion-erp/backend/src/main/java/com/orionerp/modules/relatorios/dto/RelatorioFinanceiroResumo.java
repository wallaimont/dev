package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioFinanceiroResumo(
        Long empresaId,
        String periodo,
        BigDecimal totalContasReceber,
        BigDecimal totalContasPagar,
        BigDecimal saldo,
        Long qtdTitulosVencidos
) {}
