package com.orionerp.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkflowDecisaoRequest(
        @NotNull Long aprovadorId,
        @NotBlank String decisao,
        String justificativa
) {}
