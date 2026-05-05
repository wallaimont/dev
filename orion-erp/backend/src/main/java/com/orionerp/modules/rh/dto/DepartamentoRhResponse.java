package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.DepartamentoRh;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepartamentoRhResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        Long centroCustoId,
        Long gestorId,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DepartamentoRhResponse from(DepartamentoRh d) {
        return new DepartamentoRhResponse(
                d.getId(), d.getUuid(), d.getEmpresaId(), d.getCodigo(), d.getNome(),
                d.getCentroCustoId(), d.getGestorId(), d.getAtivo(),
                d.getCreatedAt(), d.getUpdatedAt()
        );
    }
}
