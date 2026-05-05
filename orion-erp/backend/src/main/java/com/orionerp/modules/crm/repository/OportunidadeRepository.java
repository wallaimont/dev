package com.orionerp.modules.crm.repository;

import com.orionerp.modules.crm.domain.Oportunidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long>,
        JpaSpecificationExecutor<Oportunidade> {

    Optional<Oportunidade> findByIdAndDeletedFalse(Long id);
}
