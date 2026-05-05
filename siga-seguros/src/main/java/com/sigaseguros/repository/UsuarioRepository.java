package com.sigaseguros.repository;

import com.sigaseguros.entity.Usuario;
import com.sigaseguros.enums.Perfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndActiveTrue(String email);

    Optional<Usuario> findByIdAndActiveTrue(Long id);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.active = true " +
           "AND (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:perfil IS NULL OR u.perfil = :perfil)")
    Page<Usuario> findAllWithFilters(@Param("nome") String nome,
                                     @Param("email") String email,
                                     @Param("perfil") Perfil perfil,
                                     Pageable pageable);

    long countByActiveTrue();
}
