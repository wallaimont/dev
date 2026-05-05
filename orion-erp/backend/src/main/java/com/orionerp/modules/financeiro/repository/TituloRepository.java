package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.Titulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TituloRepository extends JpaRepository<Titulo, Long>, JpaSpecificationExecutor<Titulo> {

    Optional<Titulo> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndNumeroAndTipoAndDeletedFalse(
            Long empresaId, Long filialId, String numero, String tipo);
}
