package com.orionerp.modules.administration.dto;

import java.util.UUID;

public record ParametroSistemaResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        String chave,
        String valor,
        String tipo,
        String descricao,
        String modulo,
        Boolean editavel,
        Boolean ativo
) {
}
