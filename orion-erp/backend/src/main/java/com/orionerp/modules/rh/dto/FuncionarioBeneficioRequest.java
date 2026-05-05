package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FuncionarioBeneficioRequest(
        @NotNull Long empresaId,
        @NotNull Long funcionarioId,
        @NotNull Long beneficioId,
        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal valorCustomizado
) {}
