package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Comissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ComissaoRepository extends JpaRepository<Comissao, UUID> {
    Page<Comissao> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
}
