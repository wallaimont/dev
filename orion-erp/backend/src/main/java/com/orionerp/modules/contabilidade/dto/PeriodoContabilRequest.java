package com.orionerp.modules.contabilidade.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PeriodoContabilRequest(
        @NotNull Long empresaId,
        @NotNull Integer ano,
        @NotNull Integer mes,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim
) {}
