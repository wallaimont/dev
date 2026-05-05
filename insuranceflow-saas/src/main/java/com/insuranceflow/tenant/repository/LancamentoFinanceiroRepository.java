package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, UUID> {
    Page<LancamentoFinanceiro> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
}
