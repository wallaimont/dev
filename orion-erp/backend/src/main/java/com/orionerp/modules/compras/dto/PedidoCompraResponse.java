package com.orionerp.modules.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoCompraResponse(
        Long id,
        String numero,
        Long empresaId,
        Long filialId,
        Long fornecedorId,
        String fornecedorNome,
        LocalDate dataPedido,
        LocalDate dataPrevisaoEntrega,
        BigDecimal valorTotal,
        BigDecimal valorFrete,
        BigDecimal valorDesconto,
        String status,
        String observacao,
        List<ItemResponse> itens
) {
    public record ItemResponse(
            Long id,
            Long produtoId,
            String produtoCodigo,
            String produtoNome,
            BigDecimal quantidade,
            BigDecimal quantidadeRecebida,
            BigDecimal precoUnitario,
            BigDecimal valorTotal
    ) {}
}
