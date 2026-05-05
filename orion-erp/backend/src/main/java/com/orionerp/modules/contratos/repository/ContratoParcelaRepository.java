package com.orionerp.modules.contratos.repository;

import com.orionerp.modules.contratos.domain.ContratoParcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ContratoParcelaRepository extends JpaRepository<ContratoParcela, Long>, JpaSpecificationExecutor<ContratoParcela> {
    Optional<ContratoParcela> findByIdAndDeletedFalse(Long id);
    List<ContratoParcela> findByContratoIdAndDeletedFalse(Long contratoId);
}
