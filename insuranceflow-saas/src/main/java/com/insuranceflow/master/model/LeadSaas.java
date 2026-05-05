package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_saas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeadSaas extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    @Column(name = "empresa_nome")
    private String empresaNome;

    @Column(nullable = false)
    private String email;

    private String telefone;
    private String origem;
    private String interesse;

    @Column(nullable = false)
    private String status;

    private String observacoes;

    @Column(name = "data_ultimo_contato")
    private LocalDateTime dataUltimoContato;
}
