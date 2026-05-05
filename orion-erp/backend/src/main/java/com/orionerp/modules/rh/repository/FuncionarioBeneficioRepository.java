package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.FuncionarioBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FuncionarioBeneficioRepository extends JpaRepository<FuncionarioBeneficio, Long>, JpaSpecificationExecutor<FuncionarioBeneficio> {
    Optional<FuncionarioBeneficio> findByIdAndDeletedFalse(Long id);
    List<FuncionarioBeneficio> findByFuncionarioIdAndDeletedFalse(Long funcionarioId);
}
