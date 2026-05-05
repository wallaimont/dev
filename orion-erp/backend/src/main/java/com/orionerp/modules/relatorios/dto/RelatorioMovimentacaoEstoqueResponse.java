package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioMovimentacaoEstoqueResponse(
        LocalDate data,
        String produtoDescricao,
        String tipo,
        BigDecimal quantidade,
        String documento,
        String armazemOrigem,
        String armazemDestino
) {}
