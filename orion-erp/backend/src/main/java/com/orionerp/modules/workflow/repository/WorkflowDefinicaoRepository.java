package com.orionerp.modules.workflow.repository;

import com.orionerp.modules.workflow.domain.WorkflowDefinicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowDefinicaoRepository extends JpaRepository<WorkflowDefinicao, Long> {

    Optional<WorkflowDefinicao> findByIdAndAtivoTrue(Long id);

    List<WorkflowDefinicao> findByEmpresaIdAndAtivoTrueOrderByNome(Long empresaId);

    Optional<WorkflowDefinicao> findByEmpresaIdAndModuloAndEntidadeAndAtivoTrue(
            Long empresaId, String modulo, String entidade);

    boolean existsByEmpresaIdAndCodigo(Long empresaId, String codigo);
}
