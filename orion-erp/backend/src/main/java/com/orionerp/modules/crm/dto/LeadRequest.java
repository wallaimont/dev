package com.orionerp.modules.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeadRequest(
        @NotNull Long empresaId,
        @NotBlank String nome,
        String email,
        String telefone,
        String empresaLead,
        String cargo,
        String origem,
        Long responsavelId,
        String observacao
) {
}
