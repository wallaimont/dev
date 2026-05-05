package com.orionerp.modules.patrimonio.repository;

import com.orionerp.modules.patrimonio.domain.Depreciacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DepreciacaoRepository extends JpaRepository<Depreciacao, Long>, JpaSpecificationExecutor<Depreciacao> {
    Optional<Depreciacao> findByIdAndDeletedFalse(Long id);
    List<Depreciacao> findByBemPatrimonialIdAndDeletedFalse(Long bemPatrimonialId);
}
