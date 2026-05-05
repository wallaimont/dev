package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.TituloParcela;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TituloParcelaRepository extends JpaRepository<TituloParcela, Long> {

    List<TituloParcela> findByTituloIdOrderByNumeroParcela(Long tituloId);

    Optional<TituloParcela> findByTituloIdAndNumeroParcela(Long tituloId, Integer numeroParcela);
}
