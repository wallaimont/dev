package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "empresa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Empresa extends BaseEntity {

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;

    @Column(nullable = false)
    private String email;

    private String telefone;
    private String celular;
    private String website;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    @Column(name = "plano_id")
    private UUID planoId;

    @Column(nullable = false)
    private String status;

    @Column(name = "data_inicio_trial")
    private LocalDateTime dataInicioTrial;

    @Column(name = "data_fim_trial")
    private LocalDateTime dataFimTrial;

    private Boolean bloqueada;

    @Column(name = "motivo_bloqueio")
    private String motivoBloqueio;

    @Column(unique = true)
    private String slug;
}
