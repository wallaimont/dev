package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.CondicaoPagamento;

import java.time.LocalDateTime;

public record CondicaoPagamentoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        String tipo,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CondicaoPagamentoResponse from(CondicaoPagamento e) {
        return new CondicaoPagamentoResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getTipo(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
