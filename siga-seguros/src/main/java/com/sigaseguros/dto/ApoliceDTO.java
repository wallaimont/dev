package com.sigaseguros.dto;

import com.sigaseguros.enums.FormaPagamento;
import com.sigaseguros.enums.StatusApolice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApoliceDTO {

    private Long id;
    private String numeroApolice;

    private Long propostaId;
    private String propostaNumero;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;
    private String clienteNome;

    @NotNull(message = "Seguradora é obrigatória")
    private Long seguradoraId;
    private String seguradoraNome;

    private Long corretoraId;
    private String corretoraNome;

    @NotNull(message = "Ramo de seguro é obrigatório")
    private Long ramoSeguroId;
    private String ramoSeguroNome;

    private String dataEmissao;

    @NotNull(message = "Início da vigência é obrigatório")
    private String inicioVigencia;

    @NotNull(message = "Fim da vigência é obrigatório")
    private String fimVigencia;

    private BigDecimal premioTotal;
    private BigDecimal percentualComissao;
    private BigDecimal valorComissao;
    private FormaPagamento formaPagamento;
    private Integer quantidadeParcelas;
    private StatusApolice status;
    private String observacoes;
    private String responsavelInterno;
    private Boolean active;
    private String createdAt;
}
