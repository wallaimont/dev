package com.orionerp.modules.seguros.repository;

import com.orionerp.modules.seguros.domain.Seguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SeguradoraRepository extends JpaRepository<Seguradora, Long>, JpaSpecificationExecutor<Seguradora> {

    Optional<Seguradora> findByIdAndDeletedFalse(Long id);
}
