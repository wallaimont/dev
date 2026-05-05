package com.orionerp.modules.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecebimentoRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotNull String numero,
        @NotNull Long pedidoCompraId,
        String numeroNf,
        String serieNf,
        String chaveNfe,
        String observacao,
        @NotEmpty @Valid List<ItemInput> itens
) {
    public record ItemInput(
            @NotNull Long produtoId,
            Long pedidoCompraItemId,
            @NotNull @Positive BigDecimal quantidade,
            @NotNull @Positive BigDecimal precoUnitario,
            String lote,
            LocalDate validade,
            Long armazemId,
            Long localizacaoId
    ) {}
}
