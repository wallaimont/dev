package com.orionerp.modules.workflow.dto;

import com.orionerp.modules.workflow.domain.WorkflowDefinicao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkflowDefinicaoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String codigo,
        String nome,
        String descricao,
        String modulo,
        String entidade,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<WorkflowAlcadaResponse> alcadas
) {
    public static WorkflowDefinicaoResponse from(WorkflowDefinicao d) {
        List<WorkflowAlcadaResponse> alcadasDto = d.getAlcadas() == null
                ? List.of()
                : d.getAlcadas().stream().map(WorkflowAlcadaResponse::from).toList();

        return new WorkflowDefinicaoResponse(
                d.getId(),
                d.getUuid(),
                d.getEmpresaId(),
                d.getCodigo(),
                d.getNome(),
                d.getDescricao(),
                d.getModulo(),
                d.getEntidade(),
                d.getAtivo(),
                d.getCreatedAt(),
                d.getUpdatedAt(),
                alcadasDto
        );
    }
}
