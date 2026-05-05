package com.orionerp.modules.estoque.repository;

import com.orionerp.modules.estoque.domain.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long>, JpaSpecificationExecutor<Inventario> {

    Optional<Inventario> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(Long empresaId, Long filialId, String numero);
}
