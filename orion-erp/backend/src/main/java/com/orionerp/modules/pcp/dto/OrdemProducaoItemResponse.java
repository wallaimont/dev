package com.orionerp.modules.pcp.dto;

import com.orionerp.modules.pcp.domain.OrdemProducaoItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemProducaoItemResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long ordemProducaoId,
        Long produtoId,
        BigDecimal quantidadePrevista,
        BigDecimal quantidadeUtilizada,
        String unidadeMedida,
        BigDecimal custoUnitario,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrdemProducaoItemResponse from(OrdemProducaoItem e) {
        return new OrdemProducaoItemResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getOrdemProducaoId(), e.getProdutoId(),
                e.getQuantidadePrevista(), e.getQuantidadeUtilizada(),
                e.getUnidadeMedida(), e.getCustoUnitario(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
