package com.sigaseguros.repository;

import com.sigaseguros.entity.Corretora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CorretoraRepository extends JpaRepository<Corretora, Long> {

    Optional<Corretora> findByIdAndActiveTrue(Long id);

    List<Corretora> findAllByActiveTrue();

    boolean existsByCnpjAndActiveTrue(String cnpj);

    boolean existsByCnpjAndIdNotAndActiveTrue(String cnpj, Long id);

    @Query("SELECT c FROM Corretora c WHERE c.active = true " +
           "AND (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))")
    Page<Corretora> findAllWithFilters(@Param("nome") String nome, Pageable pageable);

    long countByActiveTrue();
}
