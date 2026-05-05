package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FilialRepository extends JpaRepository<Filial, Long>, JpaSpecificationExecutor<Filial> {

    Optional<Filial> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalse(Long empresaId, String codigo);

    boolean existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(Long empresaId, String codigo, Long id);

    boolean existsByCnpjAndDeletedFalse(String cnpj);

    boolean existsByCnpjAndDeletedFalseAndIdNot(String cnpj, Long id);
}
