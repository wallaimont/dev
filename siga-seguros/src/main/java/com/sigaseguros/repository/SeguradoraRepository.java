package com.sigaseguros.repository;

import com.sigaseguros.entity.Seguradora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeguradoraRepository extends JpaRepository<Seguradora, Long> {

    Optional<Seguradora> findByIdAndActiveTrue(Long id);

    List<Seguradora> findAllByActiveTrue();

    boolean existsByCnpjAndActiveTrue(String cnpj);

    boolean existsByCnpjAndIdNotAndActiveTrue(String cnpj, Long id);

    @Query("SELECT s FROM Seguradora s WHERE s.active = true " +
           "AND (:nome IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :nome, '%')))")
    Page<Seguradora> findAllWithFilters(@Param("nome") String nome, Pageable pageable);

    long countByActiveTrue();
}
