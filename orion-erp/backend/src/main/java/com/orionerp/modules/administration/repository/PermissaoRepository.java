package com.orionerp.modules.administration.repository;

import com.orionerp.modules.administration.domain.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface PermissaoRepository extends JpaRepository<Permissao, Long>, JpaSpecificationExecutor<Permissao> {

	List<Permissao> findByIdIn(Collection<Long> ids);
}
