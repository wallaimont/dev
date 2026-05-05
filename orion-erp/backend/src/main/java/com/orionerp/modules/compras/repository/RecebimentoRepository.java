package com.orionerp.modules.compras.repository;

import com.orionerp.modules.compras.domain.Recebimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecebimentoRepository extends JpaRepository<Recebimento, Long>,
        JpaSpecificationExecutor<Recebimento> {

    Optional<Recebimento> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(Long empresaId, Long filialId, String numero);
}
