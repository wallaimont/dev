package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FeriasRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long funcionarioId,
        @NotNull LocalDate periodoAquisitivoInicio,
        @NotNull LocalDate periodoAquisitivoFim,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        @NotNull Integer diasGozo,
        Integer diasAbono,
        BigDecimal valorFerias,
        BigDecimal valorAbono,
        BigDecimal valorAdiantamento13
) {}
