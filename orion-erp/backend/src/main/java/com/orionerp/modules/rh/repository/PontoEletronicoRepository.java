package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.PontoEletronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PontoEletronicoRepository extends JpaRepository<PontoEletronico, Long>, JpaSpecificationExecutor<PontoEletronico> {
    Optional<PontoEletronico> findByIdAndDeletedFalse(Long id);
    List<PontoEletronico> findByFuncionarioIdAndDataBetweenAndDeletedFalse(Long funcionarioId, LocalDate inicio, LocalDate fim);
}
