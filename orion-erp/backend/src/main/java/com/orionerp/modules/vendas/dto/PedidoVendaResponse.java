package com.orionerp.modules.vendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoVendaResponse(
        Long id,
        String numero,
        Long empresaId,
        Long filialId,
        Long clienteId,
        String clienteNome,
        Long vendedorId,
        Long transportadoraId,
        LocalDate dataPedido,
        LocalDate dataPrevisaoEntrega,
        BigDecimal valorProdutos,
        BigDecimal valorDesconto,
        BigDecimal valorFrete,
        BigDecimal valorTotal,
        String status,
        Long aprovadoPor,
        LocalDateTime aprovadoEm,
        String observacao,
        List<ItemResponse> itens
) {
    public record ItemResponse(
            Long id,
            Long produtoId,
            String produtoCodigo,
            String produtoNome,
            BigDecimal quantidade,
            BigDecimal quantidadeEntregue,
            BigDecimal precoUnitario,
            BigDecimal percentualDesconto,
            BigDecimal valorDesconto,
            BigDecimal valorTotal
    ) {}
}
