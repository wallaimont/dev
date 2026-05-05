package com.orionerp.modules.crm.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leads")
@Getter
@Setter
public class Lead extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(name = "empresa_lead", length = 200)
    private String empresaLead;

    @Column(length = 100)
    private String cargo;

    @Column(length = 50)
    private String origem;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    @Column(nullable = false, length = 20)
    private String status = "NOVO";

    @Column(name = "convertido_cliente_id")
    private Long convertidoClienteId;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
