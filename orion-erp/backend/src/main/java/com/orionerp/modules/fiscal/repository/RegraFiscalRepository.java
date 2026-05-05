package com.orionerp.modules.fiscal.repository;

import com.orionerp.modules.fiscal.domain.RegraFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RegraFiscalRepository extends JpaRepository<RegraFiscal, Long>,
        JpaSpecificationExecutor<RegraFiscal> {

    Optional<RegraFiscal> findByIdAndAtivoTrue(Long id);

    List<RegraFiscal> findByEmpresaIdAndAtivoTrueOrderById(Long empresaId);

    List<RegraFiscal> findByEmpresaIdAndUfOrigemAndUfDestinoAndAtivoTrue(
            Long empresaId, String ufOrigem, String ufDestino);
}
