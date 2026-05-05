package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubgrupoProdutoRequest(
        @NotNull Long empresaId,
        @NotNull Long grupoId,
        @NotBlank String codigo,
        @NotBlank String nome
) {}
