package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {

    Optional<Empresa> findByIdAndDeletedFalse(Long id);

    boolean existsByCodigoIgnoreCaseAndDeletedFalse(String codigo);

    boolean existsByCodigoIgnoreCaseAndDeletedFalseAndIdNot(String codigo, Long id);

    boolean existsByCnpjAndDeletedFalse(String cnpj);

    boolean existsByCnpjAndDeletedFalseAndIdNot(String cnpj, Long id);
}
