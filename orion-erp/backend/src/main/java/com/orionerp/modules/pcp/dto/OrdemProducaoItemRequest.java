package com.orionerp.modules.pcp.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrdemProducaoItemRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long ordemProducaoId,
        @NotNull Long produtoId,
        BigDecimal quantidadePrevista,
        BigDecimal quantidadeUtilizada,
        String unidadeMedida,
        BigDecimal custoUnitario
) {}
