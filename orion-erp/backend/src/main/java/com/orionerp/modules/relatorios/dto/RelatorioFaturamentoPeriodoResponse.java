package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioFaturamentoPeriodoResponse(
        String periodo,
        Long quantidadeNotas,
        BigDecimal valorTotalProdutos,
        BigDecimal valorTotalImpostos,
        BigDecimal valorTotalNotas
) {}
