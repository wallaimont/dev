package com.orionerp.modules.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoCompraRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank String numero,
        @NotNull Long fornecedorId,
        Long condicaoPagamentoId,
        LocalDate dataPrevisaoEntrega,
        BigDecimal valorFrete,
        BigDecimal valorDesconto,
        String observacao,
        @NotEmpty @Valid List<ItemInput> itens
) {
    public record ItemInput(
            @NotNull Long produtoId,
            @NotNull @Positive BigDecimal quantidade,
            @NotNull @Positive BigDecimal precoUnitario,
            Long unidadeMedidaId
    ) {}
}
