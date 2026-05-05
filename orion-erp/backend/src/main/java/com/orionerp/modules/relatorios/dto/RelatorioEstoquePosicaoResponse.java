package com.orionerp.modules.relatorios.dto;

import java.math.BigDecimal;

public record RelatorioEstoquePosicaoResponse(
        Long produtoId,
        String produtoCodigo,
        String produtoDescricao,
        String armazemNome,
        BigDecimal quantidade,
        BigDecimal custoMedio,
        BigDecimal valorTotal,
        BigDecimal reservado,
        BigDecimal disponivel
) {}
