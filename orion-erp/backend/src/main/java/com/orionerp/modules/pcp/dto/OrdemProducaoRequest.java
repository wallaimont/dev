package com.orionerp.modules.pcp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrdemProducaoRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String numero,
        @NotNull Long produtoId,
        @NotNull BigDecimal quantidade,
        LocalDate dataInicio,
        LocalDate dataPrevisaoFim,
        LocalDate dataFim,
        String status,
        String prioridade,
        Long centroCustoId,
        String observacao
) {}
