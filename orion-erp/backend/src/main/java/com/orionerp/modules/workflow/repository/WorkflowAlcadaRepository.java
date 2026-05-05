package com.orionerp.modules.workflow.repository;

import com.orionerp.modules.workflow.domain.WorkflowAlcada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowAlcadaRepository extends JpaRepository<WorkflowAlcada, Long> {

    List<WorkflowAlcada> findByWorkflowDefinicaoIdOrderByOrdemAsc(Long workflowDefinicaoId);
}
