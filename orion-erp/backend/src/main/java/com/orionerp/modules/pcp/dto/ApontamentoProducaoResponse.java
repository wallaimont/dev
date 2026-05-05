package com.orionerp.modules.pcp.dto;

import com.orionerp.modules.pcp.domain.ApontamentoProducao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApontamentoProducaoResponse(
        Long id,
        Long empresaId,
        Long filialId,
        Long ordemProducaoId,
        Long funcionarioId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        BigDecimal quantidadeProduzida,
        BigDecimal quantidadeRejeitada,
        String maquina,
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ApontamentoProducaoResponse from(ApontamentoProducao e) {
        return new ApontamentoProducaoResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(),
                e.getOrdemProducaoId(), e.getFuncionarioId(),
                e.getDataInicio(), e.getDataFim(),
                e.getQuantidadeProduzida(), e.getQuantidadeRejeitada(),
                e.getMaquina(), e.getObservacao(),
                e.getAtivo(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
