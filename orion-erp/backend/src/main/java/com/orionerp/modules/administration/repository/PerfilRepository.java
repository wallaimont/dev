package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.Perfil;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long>, JpaSpecificationExecutor<Perfil> {

	@EntityGraph(attributePaths = {"permissoes"})
	Optional<Perfil> findByIdAndDeletedFalse(Long id);

	boolean existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(Long empresaId, String codigo);

	boolean existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(Long empresaId, String codigo, Long id);
}
