package com.orionerp.modules.pcp.dto;

import com.orionerp.modules.pcp.domain.EstruturaProduto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstruturaProdutoResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long produtoPaiId,
        Long produtoFilhoId,
        BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal perdaPercentual,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EstruturaProdutoResponse from(EstruturaProduto e) {
        return new EstruturaProdutoResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getProdutoPaiId(), e.getProdutoFilhoId(),
                e.getQuantidade(), e.getUnidadeMedida(),
                e.getPerdaPercentual(), e.getObservacao(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
