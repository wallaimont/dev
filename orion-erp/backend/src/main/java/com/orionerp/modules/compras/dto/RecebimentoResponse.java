package com.orionerp.modules.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecebimentoResponse(
        Long id,
        String numero,
        Long empresaId,
        Long filialId,
        Long pedidoCompraId,
        String pedidoCompraNumero,
        Long fornecedorId,
        String fornecedorNome,
        LocalDate dataRecebimento,
        String numeroNf,
        String serieNf,
        String chaveNfe,
        BigDecimal valorTotal,
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
            BigDecimal precoUnitario,
            BigDecimal valorTotal,
            String lote,
            LocalDate validade
    ) {}
}
