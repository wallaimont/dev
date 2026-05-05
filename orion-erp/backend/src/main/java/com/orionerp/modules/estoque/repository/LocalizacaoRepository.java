package com.orionerp.modules.estoque.repository;

import com.orionerp.modules.estoque.domain.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

    List<Localizacao> findByArmazemIdAndAtivoTrue(Long armazemId);
}
