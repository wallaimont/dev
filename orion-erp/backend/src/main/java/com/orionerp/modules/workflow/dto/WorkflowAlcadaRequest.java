package com.orionerp.modules.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WorkflowAlcadaRequest(
        Integer nivel,
        @NotBlank String nome,
        Long perfilId,
        Long usuarioId,
        BigDecimal valorMinimo,
        BigDecimal valorMaximo,
        Boolean obrigatorio,
        @NotNull Integer ordem
) {}
