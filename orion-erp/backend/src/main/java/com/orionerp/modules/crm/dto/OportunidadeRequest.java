package com.orionerp.modules.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OportunidadeRequest(
        @NotNull Long empresaId,
        @NotBlank String titulo,
        Long leadId,
        Long clienteId,
        Long responsavelId,
        BigDecimal valorEstimado,
        Integer probabilidade,
        LocalDate dataPrevisaoFechamento,
        String observacao
) {
}
