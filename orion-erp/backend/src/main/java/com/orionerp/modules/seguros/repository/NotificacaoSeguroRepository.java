package com.orionerp.modules.seguros.repository;

import com.orionerp.modules.seguros.domain.NotificacaoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificacaoSeguroRepository extends JpaRepository<NotificacaoSeguro, Long>, JpaSpecificationExecutor<NotificacaoSeguro> {
}
