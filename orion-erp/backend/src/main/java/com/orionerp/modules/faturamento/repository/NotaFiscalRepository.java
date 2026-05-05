package com.orionerp.modules.faturamento.repository;

import com.orionerp.modules.faturamento.domain.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long>, JpaSpecificationExecutor<NotaFiscal> {
    Optional<NotaFiscal> findByIdAndDeletedFalse(Long id);
}
