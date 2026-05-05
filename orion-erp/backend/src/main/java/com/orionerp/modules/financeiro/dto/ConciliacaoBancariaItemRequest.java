package com.orionerp.modules.financeiro.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConciliacaoBancariaItemRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long conciliacaoId,
        Long lancamentoFinanceiroId,
        LocalDate dataExtrato,
        String descricaoExtrato,
        BigDecimal valorExtrato,
        Boolean conciliado,
        LocalDate dataConciliacao
) {}
