package com.orionerp.modules.seguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PropostaSeguroRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String numero,
        @NotNull Long clienteId,
        @NotNull Long seguradoraId,
        Long corretoraId,
        @NotBlank String ramo,
        @NotNull LocalDate vigenciaInicio,
        @NotNull LocalDate vigenciaFim,
        BigDecimal premioLiquido,
        BigDecimal premioTotal,
        BigDecimal percentualComissao,
        String responsavel,
        String observacao,
        String status
) {}
