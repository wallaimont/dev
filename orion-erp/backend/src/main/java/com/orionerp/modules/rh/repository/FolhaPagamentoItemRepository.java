package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.FolhaPagamentoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FolhaPagamentoItemRepository extends JpaRepository<FolhaPagamentoItem, Long>, JpaSpecificationExecutor<FolhaPagamentoItem> {
    Optional<FolhaPagamentoItem> findByIdAndDeletedFalse(Long id);
    List<FolhaPagamentoItem> findByFolhaPagamentoIdAndDeletedFalse(Long folhaPagamentoId);
}
