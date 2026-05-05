package com.sigaseguros.repository;

import com.sigaseguros.entity.RamoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RamoSeguroRepository extends JpaRepository<RamoSeguro, Long> {

    Optional<RamoSeguro> findByIdAndActiveTrue(Long id);

    List<RamoSeguro> findAllByActiveTrue();

    boolean existsByNomeAndActiveTrue(String nome);
}
