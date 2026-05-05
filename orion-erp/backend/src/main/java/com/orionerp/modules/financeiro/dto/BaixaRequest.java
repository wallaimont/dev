package com.orionerp.modules.financeiro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BaixaRequest(
        @NotNull Long parcelaId,
        @NotNull LocalDate dataBaixa,
        @NotNull @Positive BigDecimal valorPago,
        BigDecimal valorJuros,
        BigDecimal valorMulta,
        BigDecimal valorDesconto,
        Long contaBancariaId,
        @Size(max = 30) String formaPagamento,
        String observacao
) {
}
