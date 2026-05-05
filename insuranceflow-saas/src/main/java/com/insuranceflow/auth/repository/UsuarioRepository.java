package com.insuranceflow.auth.repository;

import com.insuranceflow.auth.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmailAndActiveTrue(String email);
    Optional<Usuario> findByIdAndActiveTrue(UUID id);
    Page<Usuario> findByEmpresaIdAndActiveTrue(UUID empresaId, Pageable pageable);
    long countByEmpresaIdAndActiveTrue(UUID empresaId);
    boolean existsByEmail(String email);
    long countByActiveTrue();
}
