package com.orionerp.modules.vendas.dto;

import com.orionerp.modules.vendas.domain.TabelaPrecoItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TabelaPrecoItemResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long tabelaPrecoId,
        Long produtoId,
        BigDecimal preco,
        BigDecimal precoPromocional,
        BigDecimal quantidadeMinima,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TabelaPrecoItemResponse from(TabelaPrecoItem e) {
        return new TabelaPrecoItemResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getTabelaPrecoId(), e.getProdutoId(),
                e.getPreco(), e.getPrecoPromocional(), e.getQuantidadeMinima(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
