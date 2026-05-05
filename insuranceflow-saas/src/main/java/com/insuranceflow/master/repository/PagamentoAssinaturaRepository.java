package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.PagamentoAssinatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PagamentoAssinaturaRepository extends JpaRepository<PagamentoAssinatura, UUID> {
    Page<PagamentoAssinatura> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    Page<PagamentoAssinatura> findByActiveTrue(Pageable pageable);
    long countByStatus(String status);

    List<PagamentoAssinatura> findByAssinaturaIdAndActiveTrueOrderByDataVencimentoDesc(UUID assinaturaId);

    List<PagamentoAssinatura> findByStatusAndDataVencimentoBeforeAndActiveTrue(String status, LocalDate data);

    Page<PagamentoAssinatura> findByStatusAndActiveTrue(String status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM PagamentoAssinatura p WHERE p.status = 'PAGO' AND p.active = true")
    BigDecimal calcularTotalRecebido();

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM PagamentoAssinatura p WHERE p.status = 'PENDENTE' AND p.active = true")
    BigDecimal calcularTotalPendente();
}
