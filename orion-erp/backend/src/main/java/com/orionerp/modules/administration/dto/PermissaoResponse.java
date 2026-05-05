package com.orionerp.modules.administration.dto;

import java.util.UUID;

public record PermissaoResponse(
        Long id,
        UUID uuid,
        String modulo,
        String recurso,
        String acao,
        String descricao
) {
}
