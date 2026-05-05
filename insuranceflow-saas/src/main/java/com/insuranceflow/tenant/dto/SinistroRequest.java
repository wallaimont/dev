package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SinistroRequest {
    @NotNull private UUID apoliceId;
    @NotNull private UUID clienteId;
    @NotNull private LocalDate dataOcorrencia;
    private LocalDate dataAviso;
    private String tipo;
    @NotBlank private String descricao;
    private BigDecimal valorEstimado;
    private String localOcorrencia;
    private String boletimOcorrencia;
    private String observacoes;
}
