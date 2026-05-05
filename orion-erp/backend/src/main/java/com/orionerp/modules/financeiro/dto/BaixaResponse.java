package com.orionerp.modules.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BaixaResponse(
        Long id,
        UUID uuid,
        Long parcelaId,
        LocalDate dataBaixa,
        BigDecimal valorPago,
        BigDecimal valorJuros,
        BigDecimal valorMulta,
        BigDecimal valorDesconto,
        Long contaBancariaId,
        String formaPagamento,
        String observacao,
        Boolean estornado,
        LocalDateTime estornadoEm,
        String estornadoPor,
        LocalDateTime createdAt
) {
}
