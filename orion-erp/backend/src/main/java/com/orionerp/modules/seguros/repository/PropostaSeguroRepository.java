package com.orionerp.modules.seguros.repository;

import com.orionerp.modules.seguros.domain.PropostaSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PropostaSeguroRepository extends JpaRepository<PropostaSeguro, Long>, JpaSpecificationExecutor<PropostaSeguro> {

    Optional<PropostaSeguro> findByIdAndDeletedFalse(Long id);
}
