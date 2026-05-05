package com.orionerp.modules.workflow.dto;

import com.orionerp.modules.workflow.domain.WorkflowAprovacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkflowAprovacaoResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long workflowDefinicaoId,
        String workflowNome,
        Long alcadaId,
        String alcadaNome,
        String entidade,
        Long entidadeId,
        Integer nivel,
        Long aprovadorId,
        String decisao,
        String justificativa,
        LocalDateTime dataDecisao,
        String status,
        LocalDateTime createdAt
) {
    public static WorkflowAprovacaoResponse from(WorkflowAprovacao a) {
        return new WorkflowAprovacaoResponse(
                a.getId(),
                a.getUuid(),
                a.getEmpresaId(),
                a.getFilialId(),
                a.getWorkflowDefinicao().getId(),
                a.getWorkflowDefinicao().getNome(),
                a.getAlcada().getId(),
                a.getAlcada().getNome(),
                a.getEntidade(),
                a.getEntidadeId(),
                a.getNivel(),
                a.getAprovadorId(),
                a.getDecisao(),
                a.getJustificativa(),
                a.getDataDecisao(),
                a.getStatus(),
                a.getCreatedAt()
        );
    }
}
