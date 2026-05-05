package com.orionerp.modules.contabilidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoContabilRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotBlank String lote,
        String sublote,
        @NotNull Integer numero,
        @NotNull LocalDate dataLancamento,
        @NotNull Long contaDebitoId,
        @NotNull Long contaCreditoId,
        @NotNull BigDecimal valor,
        @NotBlank String historico,
        String documento,
        Long centroCustoId,
        Long centroResultadoId,
        String tipo,
        String origem,
        Long origemId
) {}
