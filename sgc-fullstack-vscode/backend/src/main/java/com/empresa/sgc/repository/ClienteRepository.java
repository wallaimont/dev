package com.empresa.sgc.repository;

import com.empresa.sgc.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    boolean existsByDocumento(String documento);
}
