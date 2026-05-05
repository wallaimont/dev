package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PagamentoManualRequest {
    @NotNull private UUID assinaturaId;
    @NotNull private UUID empresaId;
    @NotNull private BigDecimal valor;
    @NotBlank private String metodoPagamento;
    private String observacoes;
}
