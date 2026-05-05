package com.orionerp.modules.contabilidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanoContasRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String descricao,
        @NotBlank String tipo,
        @NotBlank String natureza,
        String classificacao,
        Long contaPaiId,
        Integer nivel,
        Boolean aceitaLancamento
) {}
