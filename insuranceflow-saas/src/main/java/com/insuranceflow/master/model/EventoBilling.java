package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "evento_billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoBilling extends BaseEntity {

    @Column(name = "empresa_id")
    private UUID empresaId;

    @Column(name = "assinatura_id")
    private UUID assinaturaId;

    @Column(name = "pagamento_id")
    private UUID pagamentoId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String gateway;

    @Column(name = "id_evento_externo")
    private String idEventoExterno;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "mensagem_erro")
    private String mensagemErro;
}
