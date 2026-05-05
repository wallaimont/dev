package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.Ferias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FeriasRepository extends JpaRepository<Ferias, Long>, JpaSpecificationExecutor<Ferias> {
    Optional<Ferias> findByIdAndDeletedFalse(Long id);
    List<Ferias> findByFuncionarioIdAndDeletedFalse(Long funcionarioId);
}
