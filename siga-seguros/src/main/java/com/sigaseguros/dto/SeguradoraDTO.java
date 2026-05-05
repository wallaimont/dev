package com.sigaseguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeguradoraDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18)
    private String cnpj;

    @Size(max = 20)
    private String codigoInterno;

    @Size(max = 20)
    private String telefone;

    @Size(max = 200)
    private String email;

    @Size(max = 150)
    private String contatoComercial;

    private BigDecimal percentualComissaoPadrao;
    private Boolean active;
    private String createdAt;
}
