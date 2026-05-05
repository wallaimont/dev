package com.orionerp.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkflowIniciarRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotBlank String modulo,
        @NotBlank String entidade,
        @NotNull Long entidadeId
) {}
