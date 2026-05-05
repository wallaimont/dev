package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.HistoricoPlano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoPlanoRepository extends JpaRepository<HistoricoPlano, UUID> {

    List<HistoricoPlano> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId);

    Page<HistoricoPlano> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
