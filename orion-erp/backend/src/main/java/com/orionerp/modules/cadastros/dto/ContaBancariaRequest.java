package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaBancariaRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long bancoId,
        @NotBlank String agencia,
        @NotBlank String conta,
        String digito,
        String tipo,
        String descricao,
        BigDecimal saldoInicial
) {}
