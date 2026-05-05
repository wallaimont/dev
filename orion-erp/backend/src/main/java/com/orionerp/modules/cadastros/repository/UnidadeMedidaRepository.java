package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Long> {
    Optional<UnidadeMedida> findByIdAndAtivoTrue(Long id);
    boolean existsByCodigo(String codigo);
}
