package com.orionerp.modules.workflow.dto;

import com.orionerp.modules.workflow.domain.WorkflowAlcada;

import java.math.BigDecimal;

public record WorkflowAlcadaResponse(
        Long id,
        Integer nivel,
        String nome,
        Long perfilId,
        Long usuarioId,
        BigDecimal valorMinimo,
        BigDecimal valorMaximo,
        Boolean obrigatorio,
        Integer ordem
) {
    public static WorkflowAlcadaResponse from(WorkflowAlcada a) {
        return new WorkflowAlcadaResponse(
                a.getId(),
                a.getNivel(),
                a.getNome(),
                a.getPerfilId(),
                a.getUsuarioId(),
                a.getValorMinimo(),
                a.getValorMaximo(),
                a.getObrigatorio(),
                a.getOrdem()
        );
    }
}
