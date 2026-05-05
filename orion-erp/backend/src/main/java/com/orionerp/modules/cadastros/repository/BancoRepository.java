package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BancoRepository extends JpaRepository<Banco, Long> {
    Optional<Banco> findByIdAndAtivoTrue(Long id);
    boolean existsByCodigoBanco(String codigoBanco);
}
