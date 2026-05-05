package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assinatura_digital")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssinaturaDigital extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "documento_id")
    private UUID documentoId;

    @Column(name = "entidade_tipo", length = 50)
    private String entidadeTipo;

    @Column(name = "entidade_id")
    private UUID entidadeId;

    @Column(name = "signatario_nome", nullable = false, length = 200)
    private String signatarioNome;

    @Column(name = "signatario_email", nullable = false, length = 150)
    private String signatarioEmail;

    @Column(name = "signatario_cpf", length = 14)
    private String signatarioCpf;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "data_assinatura")
    private LocalDateTime dataAssinatura;

    @Column(name = "ip_assinatura", length = 50)
    private String ipAssinatura;

    @Column(name = "hash_documento", length = 500)
    private String hashDocumento;

    @Column(name = "id_externo", length = 200)
    private String idExterno;

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    public void prePersist() {
        if (status == null) status = "PENDENTE";
        if (active == null) active = true;
    }
}
