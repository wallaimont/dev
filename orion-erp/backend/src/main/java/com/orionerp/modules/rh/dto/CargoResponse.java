package com.orionerp.modules.rh.dto;

import com.orionerp.modules.rh.domain.Cargo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CargoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        String cbo,
        BigDecimal salarioBase,
        String nivel,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CargoResponse from(Cargo c) {
        return new CargoResponse(
                c.getId(), c.getUuid(), c.getEmpresaId(), c.getCodigo(), c.getNome(),
                c.getCbo(), c.getSalarioBase(), c.getNivel(), c.getAtivo(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
