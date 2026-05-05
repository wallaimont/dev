package com.orionerp.modules.pcp.dto;

import com.orionerp.modules.pcp.domain.OrdemProducao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdemProducaoResponse(
        Long id,
        Long empresaId,
        Long filialId,
        String numero,
        Long produtoId,
        BigDecimal quantidade,
        LocalDate dataInicio,
        LocalDate dataPrevisaoFim,
        LocalDate dataFim,
        String status,
        String prioridade,
        Long centroCustoId,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrdemProducaoResponse from(OrdemProducao e) {
        return new OrdemProducaoResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getNumero(), e.getProdutoId(), e.getQuantidade(),
                e.getDataInicio(), e.getDataPrevisaoFim(), e.getDataFim(),
                e.getStatus(), e.getPrioridade(), e.getCentroCustoId(),
                e.getObservacao(), e.getAtivo(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
