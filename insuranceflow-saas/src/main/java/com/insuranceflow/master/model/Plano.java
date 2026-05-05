package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plano")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Plano extends BaseEntity {

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "preco_mensal", nullable = false)
    private BigDecimal precoMensal;

    @Column(name = "preco_anual", nullable = false)
    private BigDecimal precoAnual;

    @Column(name = "limite_usuarios")
    private Integer limiteUsuarios;

    @Column(name = "limite_clientes")
    private Integer limiteClientes;

    @Column(name = "limite_propostas_mes")
    private Integer limitePropostasMes;

    @Column(name = "limite_apolices")
    private Integer limiteApolices;

    @Column(name = "limite_armazenamento_gb")
    private Integer limiteArmazenamentoGb;

    @Column(name = "portal_cliente")
    private Boolean portalCliente;

    @Column(name = "whatsapp_integrado")
    private Boolean whatsappIntegrado;

    @Column(name = "relatorios_avancados")
    private Boolean relatoriosAvancados;

    @Column(name = "white_label")
    private Boolean whiteLabel;

    @Column(name = "acesso_api")
    private Boolean acessoApi;

    @Column(name = "suporte_prioritario")
    private Boolean suportePrioritario;
}
