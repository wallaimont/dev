package com.orionerp.modules.estoque.dto;

import com.orionerp.modules.estoque.domain.Localizacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocalizacaoResponse(
    Long id,
    UUID uuid,
    Long armazemId,
    String armazemNome,
    String codigo,
    String descricao,
    Boolean ativo,
    LocalDateTime createdAt
) {
    public static LocalizacaoResponse from(Localizacao e) {
        return new LocalizacaoResponse(
            e.getId(), e.getUuid(),
            e.getArmazem().getId(), e.getArmazem().getNome(),
            e.getCodigo(), e.getDescricao(), e.getAtivo(), e.getCreatedAt());
    }
}
