package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.TicketSuporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TicketSuporteRepository extends JpaRepository<TicketSuporte, UUID> {
    Page<TicketSuporte> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Page<TicketSuporte> findByActiveTrue(Pageable pageable);
    long countByStatus(String status);
}
