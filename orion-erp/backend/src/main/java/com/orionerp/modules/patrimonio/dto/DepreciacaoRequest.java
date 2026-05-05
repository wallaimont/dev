package com.orionerp.modules.patrimonio.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DepreciacaoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long bemPatrimonialId,
        LocalDate dataDepreciacao,
        BigDecimal valorDepreciacao,
        BigDecimal valorAcumulado,
        BigDecimal valorLiquido,
        Integer mesReferencia,
        Integer anoReferencia
) {}
