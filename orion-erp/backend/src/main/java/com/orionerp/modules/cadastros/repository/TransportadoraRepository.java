package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransportadoraRepository extends JpaRepository<Transportadora, Long>, JpaSpecificationExecutor<Transportadora> {
    Optional<Transportadora> findByIdAndDeletedFalse(Long id);
    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);
}
