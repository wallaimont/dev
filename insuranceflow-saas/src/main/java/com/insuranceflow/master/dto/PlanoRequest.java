package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlanoRequest {
    @NotBlank private String nome;
    private String descricao;
    @NotNull private BigDecimal precoMensal;
    @NotNull private BigDecimal precoAnual;
    private Integer limiteUsuarios;
    private Integer limiteClientes;
    private Integer limitePropostasMes;
    private Integer limiteApolices;
    private Integer limiteArmazenamentoGb;
    private Boolean portalCliente;
    private Boolean whatsappIntegrado;
    private Boolean relatoriosAvancados;
    private Boolean whiteLabel;
    private Boolean acessoApi;
    private Boolean suportePrioritario;
}
