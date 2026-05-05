package com.orionerp.modules.rh.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record PontoEletronicoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long funcionarioId,
        @NotNull LocalDate data,
        LocalTime entrada1,
        LocalTime saida1,
        LocalTime entrada2,
        LocalTime saida2,
        LocalTime entrada3,
        LocalTime saida3,
        BigDecimal horasTrabalhadas,
        BigDecimal horasExtras,
        BigDecimal horasFalta,
        String tipo,
        Boolean aprovado
) {}
