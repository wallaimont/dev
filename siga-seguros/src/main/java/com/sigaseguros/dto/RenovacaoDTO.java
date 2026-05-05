package com.sigaseguros.dto;

import com.sigaseguros.enums.StatusRenovacao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenovacaoDTO {

    private Long id;

    @NotNull(message = "Apólice é obrigatória")
    private Long apoliceId;
    private String apoliceNumero;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;
    private String clienteNome;

    private String dataVencimento;
    private StatusRenovacao status;
    private String responsavel;
    private String dataUltimoContato;
    private String retornoCliente;
    private String observacoes;
    private Long novaPropostaId;
    private Boolean active;
    private String createdAt;
}
