package com.sigaseguros.repository;

import com.sigaseguros.entity.Cliente;
import com.sigaseguros.enums.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByIdAndActiveTrue(Long id);

    boolean existsByCpfAndActiveTrue(String cpf);

    boolean existsByCnpjAndActiveTrue(String cnpj);

    boolean existsByCpfAndIdNotAndActiveTrue(String cpf, Long id);

    boolean existsByCnpjAndIdNotAndActiveTrue(String cnpj, Long id);

    @Query("SELECT c FROM Cliente c WHERE c.active = true " +
           "AND (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "    OR LOWER(c.razaoSocial) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "    OR LOWER(c.nomeFantasia) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:cpfCnpj IS NULL OR c.cpf LIKE CONCAT('%', :cpfCnpj, '%') " +
           "    OR c.cnpj LIKE CONCAT('%', :cpfCnpj, '%')) " +
           "AND (:tipoPessoa IS NULL OR c.tipoPessoa = :tipoPessoa) " +
           "AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Cliente> findAllWithFilters(@Param("nome") String nome,
                                     @Param("cpfCnpj") String cpfCnpj,
                                     @Param("tipoPessoa") TipoPessoa tipoPessoa,
                                     @Param("email") String email,
                                     Pageable pageable);

    long countByActiveTrue();
}
