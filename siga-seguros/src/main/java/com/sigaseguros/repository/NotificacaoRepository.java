package com.sigaseguros.repository;

import com.sigaseguros.entity.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findTop20ByDestinatarioAndLidaFalseOrderByDataCriacaoDesc(String destinatario);

    Page<Notificacao> findByDestinatarioOrderByDataCriacaoDesc(String destinatario, Pageable pageable);

    long countByDestinatarioAndLidaFalse(String destinatario);
}
