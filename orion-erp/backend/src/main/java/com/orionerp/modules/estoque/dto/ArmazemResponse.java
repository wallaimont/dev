package com.orionerp.modules.estoque.dto;

import java.util.UUID;

public record ArmazemResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String codigo,
        String nome,
        String tipo,
        Boolean ativo
) {
}
