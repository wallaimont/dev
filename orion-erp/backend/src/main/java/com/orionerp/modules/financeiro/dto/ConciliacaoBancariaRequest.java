package com.orionerp.modules.financeiro.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConciliacaoBancariaRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long contaBancariaId,
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal saldoExtrato,
        BigDecimal saldoSistema,
        BigDecimal diferenca,
        String status,
        String observacao
) {}
