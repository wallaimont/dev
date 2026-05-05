package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.LeadSaas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LeadSaasRepository extends JpaRepository<LeadSaas, UUID> {
    Page<LeadSaas> findByActiveTrue(Pageable pageable);
    long countByStatus(String status);
}
