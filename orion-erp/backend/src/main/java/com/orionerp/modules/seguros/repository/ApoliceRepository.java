package com.orionerp.modules.seguros.repository;

import com.orionerp.modules.seguros.domain.Apolice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ApoliceRepository extends JpaRepository<Apolice, Long>, JpaSpecificationExecutor<Apolice> {

    Optional<Apolice> findByIdAndDeletedFalse(Long id);
}
