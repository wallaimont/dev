package com.insuranceflow.master.repository;

import com.insuranceflow.master.model.ConfiguracaoWhiteLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ConfiguracaoWhiteLabelRepository extends JpaRepository<ConfiguracaoWhiteLabel, UUID> {
    Optional<ConfiguracaoWhiteLabel> findByEmpresaId(UUID empresaId);
}
