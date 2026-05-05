package com.orionerp.modules.fiscal.repository;

import com.orionerp.modules.fiscal.domain.Cst;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CstRepository extends JpaRepository<Cst, Long> {

    Optional<Cst> findByCodigoAndAtivoTrue(String codigo);

    List<Cst> findByTipoImpostoAndAtivoTrue(String tipoImposto);

    List<Cst> findByAtivoTrue();
}
