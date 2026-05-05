package com.orionerp.modules.crm.repository;

import com.orionerp.modules.crm.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long>,
        JpaSpecificationExecutor<Lead> {

    Optional<Lead> findByIdAndDeletedFalse(Long id);
}
