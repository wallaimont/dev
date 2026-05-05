package com.orionerp.modules.contabilidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CentroResultadoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String descricao,
        String tipo,
        String responsavel
) {}
