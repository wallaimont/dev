package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long>, JpaSpecificationExecutor<ParametroSistema> {

    Optional<ParametroSistema> findFirstByEmpresaIdAndFilialIdAndChave(Long empresaId, Long filialId, String chave);

    Optional<ParametroSistema> findFirstByEmpresaIdAndFilialIdIsNullAndChave(Long empresaId, String chave);

    Optional<ParametroSistema> findFirstByEmpresaIdIsNullAndFilialIdIsNullAndChave(String chave);

    Optional<ParametroSistema> findByIdAndDeletedFalse(Long id);
}
