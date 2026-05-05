package com.orionerp.modules.vendas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoVendaRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank String numero,
        @NotNull Long clienteId,
        Long vendedorId,
        Long transportadoraId,
        Long condicaoPagamentoId,
        Long tabelaPrecoId,
        LocalDate dataPrevisaoEntrega,
        BigDecimal valorFrete,
        BigDecimal valorDesconto,
        BigDecimal percentualComissao,
        String observacao,
        @NotEmpty @Valid List<ItemInput> itens
) {
    public record ItemInput(
            @NotNull Long produtoId,
            @NotNull @Positive BigDecimal quantidade,
            @NotNull @Positive BigDecimal precoUnitario,
            BigDecimal percentualDesconto,
            Long unidadeMedidaId,
            Long armazemId,
            String observacao
    ) {}
}
