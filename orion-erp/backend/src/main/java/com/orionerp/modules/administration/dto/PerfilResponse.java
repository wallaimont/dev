package com.orionerp.modules.administration.dto;

import java.util.List;
import java.util.UUID;

public record PerfilResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        String descricao,
        Boolean admin,
        Boolean ativo,
        List<PermissaoResponse> permissoes
) {
}
