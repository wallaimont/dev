package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioContasVencidasResponse(
        String tipo,
        String clienteOuFornecedor,
        String documento,
        BigDecimal valorOriginal,
        BigDecimal valorAberto,
        LocalDate dataVencimento,
        Long diasAtraso
) {}
