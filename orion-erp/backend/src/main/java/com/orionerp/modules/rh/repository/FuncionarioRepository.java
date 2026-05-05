package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>,
        JpaSpecificationExecutor<Funcionario> {

    Optional<Funcionario> findByIdAndDeletedFalse(Long id);

    boolean existsByEmpresaIdAndCodigoAndDeletedFalse(Long empresaId, String codigo);

    boolean existsByEmpresaIdAndCpfAndDeletedFalse(Long empresaId, String cpf);
}
