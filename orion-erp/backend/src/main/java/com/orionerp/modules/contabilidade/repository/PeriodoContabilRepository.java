package com.orionerp.modules.contabilidade.repository;

import com.orionerp.modules.contabilidade.domain.PeriodoContabil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoContabilRepository extends JpaRepository<PeriodoContabil, Long> {
    Optional<PeriodoContabil> findByEmpresaIdAndAnoAndMes(Long empresaId, Integer ano, Integer mes);
    List<PeriodoContabil> findByEmpresaIdAndAnoOrderByMes(Long empresaId, Integer ano);
}
