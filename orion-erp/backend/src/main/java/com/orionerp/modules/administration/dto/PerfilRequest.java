package com.orionerp.modules.administration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PerfilRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 300) String descricao,
        Boolean admin,
        List<Long> permissaoIds,
        Boolean ativo
) {
}
