package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long>, JpaSpecificationExecutor<ContaBancaria> {

    Optional<ContaBancaria> findByIdAndDeletedFalse(Long id);
}
