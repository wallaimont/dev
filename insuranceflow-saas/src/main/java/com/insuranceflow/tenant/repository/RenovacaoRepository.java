package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Renovacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RenovacaoRepository extends JpaRepository<Renovacao, UUID> {
    Page<Renovacao> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
}
