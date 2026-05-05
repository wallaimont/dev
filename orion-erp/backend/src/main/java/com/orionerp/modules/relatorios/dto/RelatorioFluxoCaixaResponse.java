package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioFluxoCaixaResponse(
        LocalDate data,
        BigDecimal entradas,
        BigDecimal saidas,
        BigDecimal saldo,
        BigDecimal saldoAcumulado
) {}
