package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.EventoBilling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventoBillingRepository extends JpaRepository<EventoBilling, UUID> {

    Page<EventoBilling> findByActiveTrue(Pageable pageable);

    List<EventoBilling> findByEmpresaIdAndActiveTrueOrderByCreatedAtDesc(UUID empresaId);

    boolean existsByIdEventoExterno(String idEventoExterno);
}
