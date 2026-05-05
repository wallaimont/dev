package com.orionerp.modules.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArmazemRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 20) String tipo,
        Boolean ativo
) {
}
