package com.orionerp.modules.fiscal.repository;

import com.orionerp.modules.fiscal.domain.Cfop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CfopRepository extends JpaRepository<Cfop, Long> {

    Optional<Cfop> findByCodigoAndAtivoTrue(String codigo);

    List<Cfop> findByTipoAndAtivoTrue(String tipo);

    List<Cfop> findByAtivoTrue();
}
