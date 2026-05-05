package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.TituloBaixa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TituloBaixaRepository extends JpaRepository<TituloBaixa, Long> {

    List<TituloBaixa> findByParcelaIdAndEstornadoFalse(Long parcelaId);
}
