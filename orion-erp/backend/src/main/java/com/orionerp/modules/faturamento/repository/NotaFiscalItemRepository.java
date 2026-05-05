package com.orionerp.modules.faturamento.repository;

import com.orionerp.modules.faturamento.domain.NotaFiscalItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaFiscalItemRepository extends JpaRepository<NotaFiscalItem, Long> {
    List<NotaFiscalItem> findByNotaFiscalIdAndDeletedFalse(Long notaFiscalId);
    Optional<NotaFiscalItem> findByIdAndDeletedFalse(Long id);
}
