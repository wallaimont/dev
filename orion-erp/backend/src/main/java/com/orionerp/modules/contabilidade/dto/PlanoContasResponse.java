package com.orionerp.modules.contabilidade.dto;

import com.orionerp.modules.contabilidade.domain.PlanoContas;

import java.time.LocalDateTime;
import java.util.UUID;

public record PlanoContasResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String descricao,
        String tipo,
        String natureza,
        String classificacao,
        Long contaPaiId,
        Integer nivel,
        Boolean aceitaLancamento,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PlanoContasResponse from(PlanoContas e) {
        return new PlanoContasResponse(
                e.getId(), e.getUuid(), e.getEmpresaId(),
                e.getCodigo(), e.getDescricao(), e.getTipo(), e.getNatureza(),
                e.getClassificacao(), e.getContaPaiId(), e.getNivel(),
                e.getAceitaLancamento(), e.getAtivo(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
