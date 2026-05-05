package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.ConciliacaoBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ConciliacaoBancariaRepository extends JpaRepository<ConciliacaoBancaria, Long>, JpaSpecificationExecutor<ConciliacaoBancaria> {
    Optional<ConciliacaoBancaria> findByIdAndDeletedFalse(Long id);
}
