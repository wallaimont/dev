package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long>, JpaSpecificationExecutor<Fornecedor> {

    Optional<Fornecedor> findByIdAndDeletedFalse(Long id);
}
