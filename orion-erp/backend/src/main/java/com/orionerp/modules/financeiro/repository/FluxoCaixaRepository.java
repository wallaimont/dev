package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.FluxoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FluxoCaixaRepository extends JpaRepository<FluxoCaixa, Long>, JpaSpecificationExecutor<FluxoCaixa> {
}
