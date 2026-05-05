package com.insuranceflow.tenant.repository;

import com.insuranceflow.tenant.model.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    Page<Notificacao> findByUsuarioIdAndActiveTrue(UUID usuarioId, Pageable pageable);
    long countByUsuarioIdAndLidaFalse(UUID usuarioId);
}
