package com.orionerp.modules.workflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WorkflowDefinicaoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        @NotBlank String modulo,
        @NotBlank String entidade,
        List<@Valid WorkflowAlcadaRequest> alcadas
) {}
