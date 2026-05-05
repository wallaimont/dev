package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.NaturezaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NaturezaFinanceiraRepository extends JpaRepository<NaturezaFinanceira, Long>, JpaSpecificationExecutor<NaturezaFinanceira> {

    Optional<NaturezaFinanceira> findByIdAndDeletedFalse(Long id);
}
