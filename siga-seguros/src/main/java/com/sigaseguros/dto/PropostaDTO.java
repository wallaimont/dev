package com.sigaseguros.dto;

import com.sigaseguros.enums.StatusProposta;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropostaDTO {

    private Long id;
    private String numeroProposta;

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

    private String vigenciaInicial;
    private String vigenciaFinal;
    private BigDecimal premioLiquido;
    private BigDecimal premioTotal;
    private BigDecimal percentualComissao;
    private BigDecimal valorComissao;
    private StatusProposta status;
    private String observacoes;
    private String responsavelInterno;
    private Boolean active;
    private String createdAt;
}
