package com.orionerp.modules.seguros.repository;

import com.orionerp.modules.seguros.domain.Corretora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CorretoraRepository extends JpaRepository<Corretora, Long>, JpaSpecificationExecutor<Corretora> {

    Optional<Corretora> findByIdAndDeletedFalse(Long id);
}
