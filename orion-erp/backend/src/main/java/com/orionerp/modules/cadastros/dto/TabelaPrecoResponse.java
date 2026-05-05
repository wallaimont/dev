package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.TabelaPreco;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TabelaPrecoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TabelaPrecoResponse from(TabelaPreco e) {
        return new TabelaPrecoResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getVigenciaInicio(),
                e.getVigenciaFim(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
