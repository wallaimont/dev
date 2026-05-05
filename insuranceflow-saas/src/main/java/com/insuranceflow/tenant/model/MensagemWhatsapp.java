package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensagem_whatsapp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MensagemWhatsapp extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "telefone_destino", nullable = false, length = 20)
    private String telefoneDestino;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(length = 30)
    private String tipo;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "id_mensagem_externa", length = 200)
    private String idMensagemExterna;

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    public void prePersist() {
        if (status == null) status = "PENDENTE";
        if (active == null) active = true;
    }
}
