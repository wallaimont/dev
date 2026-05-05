package com.sigaseguros.dto;

import com.sigaseguros.enums.StatusComissao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComissaoDTO {

    private Long id;
    private String origem;
    private Long propostaId;
    private Long apoliceId;
    private String apoliceNumero;
    private Long seguradoraId;
    private String seguradoraNome;
    private Long corretoraId;
    private String corretoraNome;
    private String favorecido;
    private BigDecimal percentual;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    private StatusComissao status;
    private String dataPrevista;
    private String dataRecebimento;
    private String observacoes;
    private Boolean active;
    private String createdAt;
}
