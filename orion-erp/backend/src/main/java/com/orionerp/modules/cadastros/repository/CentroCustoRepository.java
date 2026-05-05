package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.CentroCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long>, JpaSpecificationExecutor<CentroCusto> {

    Optional<CentroCusto> findByIdAndDeletedFalse(Long id);
}
