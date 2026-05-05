package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    Page<Empresa> findByActiveTrue(Pageable pageable);
    Optional<Empresa> findBySlug(String slug);
    Optional<Empresa> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    long countByStatus(String status);
    long countByActiveTrue();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.status = 'TRIAL' AND e.active = true")
    long countTrialAtivas();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.status = 'ATIVA' AND e.active = true")
    long countAtivas();
}
