package com.orionerp.modules.vendas.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TabelaPrecoItemRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long tabelaPrecoId,
        @NotNull Long produtoId,
        BigDecimal preco,
        BigDecimal precoPromocional,
        BigDecimal quantidadeMinima
) {}
