package com.sigaseguros.repository;

import com.sigaseguros.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByIdAndActiveTrue(Long id);

    List<Documento> findByClienteIdAndActiveTrue(Long clienteId);

    List<Documento> findByPropostaIdAndActiveTrue(Long propostaId);

    List<Documento> findByApoliceIdAndActiveTrue(Long apoliceId);

    List<Documento> findBySinistroIdAndActiveTrue(Long sinistroId);
}
