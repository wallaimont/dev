package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioVendasPeriodoResponse(
        String periodo,
        Long quantidadePedidos,
        BigDecimal valorTotalBruto,
        BigDecimal valorTotalDesconto,
        BigDecimal valorTotalLiquido
) {}
