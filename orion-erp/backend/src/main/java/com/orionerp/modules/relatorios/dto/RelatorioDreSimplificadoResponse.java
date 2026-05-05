package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioDreSimplificadoResponse(
        String periodo,
        BigDecimal receitaBruta,
        BigDecimal deducoes,
        BigDecimal receitaLiquida,
        BigDecimal custos,
        BigDecimal lucroBruto,
        BigDecimal despesasOperacionais,
        BigDecimal resultadoOperacional,
        BigDecimal resultadoFinanceiro,
        BigDecimal resultadoAntesIr,
        BigDecimal provisaoIr,
        BigDecimal lucroLiquido
) {}
