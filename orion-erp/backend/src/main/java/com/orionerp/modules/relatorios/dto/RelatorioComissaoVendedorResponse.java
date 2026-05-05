package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioComissaoVendedorResponse(
        Long vendedorId,
        String vendedorNome,
        BigDecimal totalVendas,
        BigDecimal totalComissaoPendente,
        BigDecimal totalComissaoPago
) {}
