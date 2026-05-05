package com.orionerp.modules.estoque.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InventarioResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long armazemId,
        String armazemNome,
        String numero,
        LocalDate dataInventario,
        Long responsavelId,
        String status,
        String observacao,
        List<ItemResponse> itens
) {
    public record ItemResponse(
            Long id,
            Long produtoId,
            String produtoCodigo,
            String produtoNome,
            String lote,
            BigDecimal quantidadeSistema,
            BigDecimal quantidadeContada,
            BigDecimal diferenca,
            Boolean ajustado
    ) {
    }
}
