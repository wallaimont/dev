package com.orionerp.modules.workflow.repository;

import com.orionerp.modules.workflow.domain.WorkflowAprovacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowAprovacaoRepository extends JpaRepository<WorkflowAprovacao, Long> {

    List<WorkflowAprovacao> findByEntidadeAndEntidadeIdOrderByNivelAsc(String entidade, Long entidadeId);

    List<WorkflowAprovacao> findByAprovadorIdAndStatusOrderByCreatedAtDesc(Long aprovadorId, String status);
}
