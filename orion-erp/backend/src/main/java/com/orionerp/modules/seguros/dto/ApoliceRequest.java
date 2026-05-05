package com.orionerp.modules.seguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApoliceRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String numero,
        Long propostaId,
        @NotNull Long clienteId,
        @NotNull Long seguradoraId,
        Long corretoraId,
        @NotBlank String ramo,
        @NotNull LocalDate vigenciaInicio,
        @NotNull LocalDate vigenciaFim,
        BigDecimal premioTotal,
        BigDecimal importanciaSegurada,
        BigDecimal franquia,
        BigDecimal percentualComissao,
        String certificadoInclusao,
        String observacao,
        String status
) {}
