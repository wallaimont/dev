package com.orionerp.modules.pcp.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EstruturaProdutoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long produtoPaiId,
        @NotNull Long produtoFilhoId,
        @NotNull BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal perdaPercentual,
        String observacao
) {}
