package com.orionerp.modules.crm.repository;

import com.orionerp.modules.crm.domain.AtividadeCrm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtividadeCrmRepository extends JpaRepository<AtividadeCrm, Long> {

    List<AtividadeCrm> findByEmpresaIdAndLeadIdOrderByDataHoraDesc(Long empresaId, Long leadId);

    List<AtividadeCrm> findByEmpresaIdAndOportunidadeIdOrderByDataHoraDesc(Long empresaId, Long oportunidadeId);

    List<AtividadeCrm> findByEmpresaIdAndClienteIdOrderByDataHoraDesc(Long empresaId, Long clienteId);

    List<AtividadeCrm> findByEmpresaIdAndResponsavelIdAndConcluidaFalseOrderByDataHoraAsc(Long empresaId, Long responsavelId);
}
