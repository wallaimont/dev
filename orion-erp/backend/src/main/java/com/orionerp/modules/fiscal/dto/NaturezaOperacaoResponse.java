package com.orionerp.modules.fiscal.dto;

import com.orionerp.modules.fiscal.domain.NaturezaOperacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record NaturezaOperacaoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        String tipo,
        Long cfopId,
        String cfopCodigo,
        String cfopDescricao,
        Boolean geraFinanceiro,
        Boolean movimentaEstoque,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NaturezaOperacaoResponse from(NaturezaOperacao entity) {
        return new NaturezaOperacaoResponse(
                entity.getId(),
                entity.getUuid(),
                entity.getEmpresaId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getTipo(),
                entity.getCfop() != null ? entity.getCfop().getId() : null,
                entity.getCfop() != null ? entity.getCfop().getCodigo() : null,
                entity.getCfop() != null ? entity.getCfop().getDescricao() : null,
                entity.getGeraFinanceiro(),
                entity.getMovimentaEstoque(),
                entity.getAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
