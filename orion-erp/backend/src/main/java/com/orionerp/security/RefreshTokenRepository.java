package com.orionerp.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndRevogadoFalse(String token);

    void deleteByUsuario_Id(Long usuarioId);
}
