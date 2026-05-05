package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Banco;

import java.time.LocalDateTime;
import java.util.UUID;

public record BancoResponse(
        Long id,
        UUID uuid,
        String codigoBanco,
        String nome,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BancoResponse from(Banco e) {
        return new BancoResponse(
                e.getId(),
                e.getUuid(),
                e.getCodigoBanco(),
                e.getNome(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
