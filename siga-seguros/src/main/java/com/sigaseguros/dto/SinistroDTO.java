package com.sigaseguros.dto;

import com.sigaseguros.enums.StatusSinistro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SinistroDTO {

    private Long id;
    private String numeroSinistro;

    @NotNull(message = "Apólice é obrigatória")
    private Long apoliceId;
    private String apoliceNumero;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;
    private String clienteNome;

    @NotNull(message = "Seguradora é obrigatória")
    private Long seguradoraId;
    private String seguradoraNome;

    @NotBlank(message = "Data do aviso é obrigatória")
    private String dataAviso;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private BigDecimal valorEstimado;
    private BigDecimal valorPago;
    private StatusSinistro status;
    private String responsavelInterno;
    private String observacoes;
    private Boolean active;
    private String createdAt;
}
